/**
 * Cerberus Copyright (C) 2013 - 2017 cerberustesting
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This file is part of Cerberus.
 *
 * Cerberus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cerberus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cerberus.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cerberus.servlet.crud.test.testcase;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.crud.entity.Application;
import org.cerberus.crud.entity.CountryEnvParam;
import org.cerberus.crud.entity.CountryEnvironmentParameters;
import org.cerberus.crud.entity.Invariant;
import org.cerberus.crud.entity.TestCase;
import org.cerberus.crud.entity.TestCaseCountry;
import org.cerberus.crud.entity.TestCaseStep;
import org.cerberus.crud.entity.TestCaseStepAction;
import org.cerberus.crud.factory.IFactoryTestCase;
import org.cerberus.crud.factory.IFactoryTestCaseCountry;
import org.cerberus.crud.factory.IFactoryTestCaseStep;
import org.cerberus.crud.factory.IFactoryTestCaseStepAction;
import org.cerberus.crud.factory.impl.FactoryTestCase;
import org.cerberus.crud.service.IApplicationService;
import org.cerberus.crud.service.ICountryEnvParamService;
import org.cerberus.crud.service.ICountryEnvironmentParametersService;
import org.cerberus.crud.service.IInvariantService;
import org.cerberus.crud.service.ITestCaseCountryService;
import org.cerberus.crud.service.ITestCaseService;
import org.cerberus.engine.entity.MessageEvent;
import org.cerberus.enums.MessageEventEnum;
import org.cerberus.exception.CerberusException;
import org.cerberus.util.StringUtil;
import org.cerberus.util.VersionComparator;
import org.cerberus.util.answer.Answer;
import org.cerberus.util.answer.AnswerUtil;
import org.cerberus.version.Infos;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author bcivel
 */
@WebServlet(name = "ImportTestCaseFromSIDE", urlPatterns = {"/ImportTestCaseFromSIDE"})
public class ImportTestCaseFromSIDE extends HttpServlet {

    private static final Logger LOG = LogManager.getLogger(ImportTestCaseFromSIDE.class);
    private ITestCaseService testcaseService;
    private IApplicationService applicationService;
    private ICountryEnvironmentParametersService countryEnvironmentParametersService;
    private IInvariantService invariantService;
    private IFactoryTestCase testcaseFactory;
    private IFactoryTestCaseCountry testcaseCountryFactory;
    private IFactoryTestCaseStep testcaseStepFactory;
    private IFactoryTestCaseStepAction testcaseStepActionFactory;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws ServletException, IOException {

        JSONObject jsonResponse = new JSONObject();
        try {
            try {
                ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
                testcaseService = appContext.getBean(ITestCaseService.class);
                testcaseFactory = appContext.getBean(IFactoryTestCase.class);
                testcaseStepFactory = appContext.getBean(IFactoryTestCaseStep.class);
                testcaseStepActionFactory = appContext.getBean(IFactoryTestCaseStepAction.class);
                applicationService = appContext.getBean(IApplicationService.class);
                invariantService = appContext.getBean(IInvariantService.class);
                testcaseCountryFactory = appContext.getBean(IFactoryTestCaseCountry.class);
                countryEnvironmentParametersService = appContext.getBean(ICountryEnvironmentParametersService.class);

                Answer ans = new Answer();
                MessageEvent msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
                msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", ""));
                ans.setResultMessage(msg);

                ///Get files
//                List<String> files = getFiles(httpServletRequest);
                HashMap<String, String> param = getParams(httpServletRequest);
                String userCreated = httpServletRequest.getUserPrincipal().getName();

                // Prepare the final answer.
                MessageEvent msg1 = new MessageEvent(MessageEventEnum.GENERIC_OK);
                Answer finalAnswer = new Answer(msg1);

                String targetFolder = param.get("test");
                String targetApplication = param.get("application");

                LOG.debug("Requested Test Folder : " + targetFolder);
                LOG.debug("Requested Test Application : " + targetApplication);

                List<Invariant> countries = invariantService.readByIdName("COUNTRY");
                Application app = applicationService.convert(applicationService.readByKey(targetApplication));
                List<CountryEnvironmentParameters> envParams = countryEnvironmentParametersService.convert(countryEnvironmentParametersService.readByVarious(null, null, null, targetApplication));
                List<String> urls = new ArrayList<>();
                for (CountryEnvironmentParameters envParam : envParams) {
                    urls.add(envParam.getIp());
                }

                for (Map.Entry<String, String> entry : param.entrySet()) {
                    String key = entry.getKey();
                    String val = entry.getValue();
                    if (key.startsWith("file")) {

                        JSONObject json = new JSONObject(val);

                        if (isCompatible(json)) {

                            String masterSIDEURL = json.getString("url");
                            JSONArray testList = new JSONArray();
                            testList = json.getJSONArray("tests");
                            for (int i = 0; i < testList.length(); i++) {

                                JSONObject test = new JSONObject();
                                test = testList.getJSONObject(i);
                                LOG.debug("importing :" + i + " : " + test.toString());

                                // Dynamically get a new testcase ID.
                                String targetTestcase = testcaseService.getMaxNumberTestCase(targetFolder);
                                TestCase newTC = testcaseFactory.create(targetFolder, targetTestcase, test.getString("name"));
                                newTC.setComment("Imported from Selenium IDE. Test ID : " + test.getString("id"));
                                newTC.setApplication(targetApplication);
                                newTC.setType(TestCase.TESTCASE_TYPE_AUTOMATED);
                                newTC.setConditionOperator("always");
                                newTC.setOrigine("Selenium IDE");
                                newTC.setRefOrigine(test.getString("id"));
                                newTC.setStatus("WORKING");
                                newTC.setUsrCreated(userCreated);

                                for (Invariant country : countries) {
                                    newTC.appendTestCaseCountries(testcaseCountryFactory.create(targetFolder, targetTestcase, country.getValue()));
                                }
                                // Step
                                TestCaseStep newStep = testcaseStepFactory.create(targetFolder, targetTestcase, 1, 1, TestCaseStep.LOOP_ONCEIFCONDITIONTRUE, "always", "", "", "", "", false, null, null, 0, false, false, userCreated, null, null, null);

                                // Action
                                for (int j = 0; j < test.getJSONArray("commands").length(); j++) {
                                    JSONObject command = test.getJSONArray("commands").getJSONObject(j);
                                    TestCaseStepAction newA = getActionFromSIDE(command, (j + 1), masterSIDEURL, urls, targetFolder, targetTestcase);
                                    if (newA != null) {
                                        newStep.appendActions(newA);
                                    }
                                }

                                newTC.appendSteps(newStep);

                                testcaseService.importWithDependency(newTC);

                            }
                        } else {
                            msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_EXPECTED);
                            msg.setDescription(msg.getDescription().replace("%ITEM%", "TestCase ")
                                    .replace("%OPERATION%", "Import")
                                    .replace("%REASON%", "The file you're trying to import is not supported or is not in a compatible version format."));
                            ans.setResultMessage(msg);
                            finalAnswer = AnswerUtil.agregateAnswer(finalAnswer, (Answer) ans);
                        }

                    }
                }

                jsonResponse.put("messageType", finalAnswer.getResultMessage().getMessage().getCodeString());
                jsonResponse.put("message", finalAnswer.getResultMessage().getDescription());

            } catch (Exception ex) {
                jsonResponse.put("messageType", MessageEventEnum.GENERIC_ERROR.getCodeString());
                jsonResponse.put("message", MessageEventEnum.GENERIC_ERROR.getDescription().replace("%REASON%", ex.toString()));
                LOG.error("General Exception during testcase import.", ex);
            }
        } catch (JSONException e) {
            LOG.error("JSONException during testcase import.", e);
            //returns a default error message with the json format that is able to be parsed by the client-side
            httpServletResponse.setContentType("application/json");
            httpServletResponse.getWriter().print(AnswerUtil.createGenericErrorAnswer());
        }
        httpServletResponse.getWriter().print(jsonResponse.toString());
    }

    private TestCaseStepAction getActionFromSIDE(JSONObject command, Integer i, String masterSIDEURL, List<String> applicationURLs, String targetFolder, String targetTestcase) {
        TestCaseStepAction newAction = null;
        try {
            String action = null;
            String value1 = "";
            String value2 = "";
            String description = command.getString("comment");
            String cond = TestCaseStepAction.CONDITIONOPERATOR_ALWAYS;
            String commandS = command.getString("command");
            if (commandS.startsWith("//")) {
                cond = TestCaseStepAction.CONDITIONOPERATOR_NEVER;
                commandS = commandS.substring(2);
            }

            switch (commandS) {
                case "setWindowSize":
                case "mouseOut":
                    // Those commands are ignored.
                    break;
                case "open":
                    LOG.debug(masterSIDEURL);
                    LOG.debug(applicationURLs);
                    value1 = masterSIDEURL + command.getString("target");
                    if (!isURLInApplication(value1, applicationURLs)) {
                        action = TestCaseStepAction.ACTION_OPENURL;
                    } else {
                        action = TestCaseStepAction.ACTION_OPENURLWITHBASE;
                        value1 = command.getString("target");
                    }
                    break;
                case "type":
                    action = TestCaseStepAction.ACTION_TYPE;
                    value1 = convertElement(command);
                    value2 = command.getString("value");
                    break;
                case "click":
                    action = TestCaseStepAction.ACTION_CLICK;
                    value1 = convertElement(command);
                    break;
                case "mouseDown":
                    action = TestCaseStepAction.ACTION_MOUSELEFTBUTTONPRESS;
                    value1 = convertElement(command);
                    break;
                case "sendKeys":
                    action = TestCaseStepAction.ACTION_KEYPRESS;
                    value1 = convertElement(command);
                    value2 = mappKey(command.getString("value"));
                    break;
                case "mouseUp":
                    action = TestCaseStepAction.ACTION_MOUSELEFTBUTTONRELEASE;
                    value1 = convertElement(command);
                    break;
                case "mouseOver":
                    action = TestCaseStepAction.ACTION_MOUSEOVER;
                    value1 = convertElement(command);
                    break;
                default:
                    action = TestCaseStepAction.ACTION_DONOTHING;
                    description = "Unknow Selenium IDE command '" + commandS + "'";
                    if (!StringUtil.isNullOrEmpty(command.getString("target"))) {
                        description += " on target '" + convertElement(command) + "'";
                    }
                    if (!StringUtil.isNullOrEmpty(command.getString("value"))) {
                        description += " with value '" + command.getString("value") + "'";
                    }
                    if (!StringUtil.isNullOrEmpty(command.getString("comment"))) {
                        description += " - " + command.getString("comment");
                    }
            }
            if (action != null) {
                newAction = testcaseStepActionFactory.create(targetFolder, targetTestcase, 1, i, i, TestCaseStepAction.CONDITIONOPERATOR_ALWAYS, "", "", "", action, value1, value2, "", false, description, null);
            }
        } catch (JSONException ex) {
            LOG.error(ex, ex);
        }
        return newAction;

    }

    private static String convertElement(JSONObject command) throws JSONException {
        String target = command.getString("target");
        if (target.startsWith("name=") || target.startsWith("xpath=") || target.startsWith("id=")) {
            return target;
        }
        JSONArray targets = command.getJSONArray("targets");
        for (int i = 0; i < targets.length(); i++) {
            if (targets.getJSONArray(i).getString(0).startsWith("xpath=")) {
                return targets.getJSONArray(i).getString(0);
            }
        }
        return target;

    }

    private static String mappKey(String key) throws JSONException {
        switch (key) {
            case "${KEY_ENTER}":
                return "ENTER";
            default:
                return key;
        }
    }

    private static boolean isURLInApplication(String url, List<String> appURLs) throws JSONException {
        String cleanedUrl = StringUtil.addSuffixIfNotAlready(StringUtil.removeProtocolFromHostURL(url), "/");
        for (String appURL : appURLs) {
            appURL = StringUtil.addSuffixIfNotAlready(StringUtil.removeProtocolFromHostURL(appURL), "/");
            LOG.debug(appURL + " - " + cleanedUrl);
            if (appURL.equalsIgnoreCase(cleanedUrl)) {
                return true;
            }
        }
        return false;
    }

    public static JSONObject parseJSONFile(String filename) throws JSONException, IOException {
        String content = new String(Files.readAllBytes(Paths.get(filename)));
        return new JSONObject(content);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private List<String> getFiles(HttpServletRequest httpServletRequest) {
        List<String> result = new ArrayList<>();

        try {
            if (ServletFileUpload.isMultipartContent(httpServletRequest)) {
                DiskFileItemFactory factory = new DiskFileItemFactory();

                ServletContext servletContext = this.getServletConfig().getServletContext();
                File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
                factory.setRepository(repository);

                ServletFileUpload upload = new ServletFileUpload(factory);

                List<FileItem> formItems = upload.parseRequest(httpServletRequest);
                if (formItems != null) {
                    LOG.debug("Nb of Files to import : " + formItems.size());
                    if (formItems.size() > 0) {
                        int i = 1;
                        for (FileItem item : formItems) {
                            LOG.debug("File to import (" + i++ + ") : " + item.toString() + " FieldName : " + item.getFieldName() + " ContentType : " + item.getContentType());
                            if (!item.isFormField()) {
                                result.add(item.getString());
                            }
                        }
                    }
                }
            }
        } catch (FileUploadException ex) {
            java.util.logging.Logger.getLogger(ImportTestCaseFromSIDE.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOG.debug("result : " + result.size());
        return result;
    }

    private HashMap<String, String> getParams(HttpServletRequest httpServletRequest) {
        HashMap<String, String> result = new HashMap<>();

        try {
            if (ServletFileUpload.isMultipartContent(httpServletRequest)) {
                DiskFileItemFactory factory = new DiskFileItemFactory();

                ServletContext servletContext = this.getServletConfig().getServletContext();
                File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
                factory.setRepository(repository);

                ServletFileUpload upload = new ServletFileUpload(factory);

                List<FileItem> formItems = upload.parseRequest(httpServletRequest);
                if (formItems != null) {
                    LOG.debug("Nb of Param to import : " + formItems.size());
                    if (formItems.size() > 0) {
                        int i = 1;
                        for (FileItem item : formItems) {
                            LOG.debug("Param to import (" + i++ + ") : " + item.toString() + " FieldName : " + item.getFieldName() + " ContentType : " + item.getContentType());
                            if (item.isFormField()) {
                                result.put(item.getFieldName(), item.getString());
                            } else {
                                result.put(item.getFieldName() + i, item.getString());

                            }
                        }
                    }
                }
            }
        } catch (FileUploadException ex) {
            java.util.logging.Logger.getLogger(ImportTestCaseFromSIDE.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOG.debug("result Param : " + result.size());
        return result;
    }

    private boolean isCompatible(JSONObject json) {

        try {
            if (!json.has("version")) {
                return false;
            }
            String fileVersion = json.getString("version");
            LOG.debug("Version from import file : " + fileVersion);

            return true;

            //Compatibility Matrix. To update if testcase (including dependencies) model change.
        } catch (JSONException ex) {
            LOG.warn(ex);
            return false;
        }
    }

}
