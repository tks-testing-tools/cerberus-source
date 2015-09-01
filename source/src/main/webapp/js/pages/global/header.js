/*
 * Cerberus  Copyright (C) 2013  vertigo17
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

function displayHeaderLabel(doc) {
    var user = getUser();
    displayMenuItem(doc);
    $("#headerUserName").html(user.login);
    var systems = getSystem();
    for (var s in systems) {
        $("#MySystem").append($('<option></option>').text(systems[s].value).val(systems[s].value));
    }
    var languages = getLanguageFromSessionStorage();
    for (var l in languages) {
        $("#MyLang").append($('<option></option>').text(languages[l].description).val(languages[l].value));
        if (languages[l].value === "en") {
            $("option[value=" + languages[l].value + "]").prepend($('<span class="flag gb"></span>'));
        } else {
            $("option[value=" + languages[l].value + "]").prepend($('<span class="flag ' + languages[l].value + '"></span>'));
        }
    }
    $("#MyLang option[value=" + user.language + "]").attr("selected", "selected");
    $("#MySystem option[value=" + user.defaultSystem + "]").attr("selected", "selected");
}

function ChangeLanguage() {
    var select = document.getElementById("MyLang");
    var selectValue = select.options[select.selectedIndex].value;
    var user = getUser();

    $.ajax({url: "UpdateUser",
        data: {id: user.login, columnPosition: "8", value: selectValue},
        async: false,
        success: function () {
            sessionStorage.clear();
            location.reload();
        }
    });
}

function ChangeSystem() {
    var select = document.getElementById("MySystem");
    var selectValue = select.options[select.selectedIndex].value;
    var user = getUser();

    $.ajax({url: "UpdateUser",
        data: {id: user.login, columnPosition: "5", value: selectValue},
        async: false,
        success: function () {
            sessionStorage.removeItem("user");
            location.reload();
        }
    });
}

function displayMenuItem(doc) {
    var menuItems = document.getElementsByName('menu');

    $(menuItems).each(function () {
        var id = $(this).attr('id');
            if ($(this).attr('class') === "dropdown-toggle") {
                $(this).html(doc.getDocLabel("page_header", id) + " <span class=\"caret\"></span>");
            } else {
                $(this).html(doc.getDocLabel("page_header", id));
        }
    });
}

function readSystem() {
    $.ajax({url: "FindInvariantByID",
        data: {idName: "SYSTEM"},
        async: false,
        dataType: 'json',
        success: function (data) {
            var sys = data;
            sessionStorage.setItem("systems", JSON.stringify(sys));
        }
    });
}

/**
 * Get the documentation from sessionStorage
 * @returns {JSONObject} Full documentation in defined language from sessionStorage
 */
function getSystem() {
    var sys;

    if (sessionStorage.getItem("sys") === null) {
        readSystem();
    }
    sys = sessionStorage.getItem("systems");
    sys = JSON.parse(sys);
    return sys;
}

function readLanguage() {
    $.ajax({url: "FindInvariantByID",
        data: {idName: "LANGUAGE"},
        async: false,
        dataType: 'json',
        success: function (data) {
            var lang = data;
            sessionStorage.setItem("language", JSON.stringify(lang));
        }
    });
}

/**
 * Get the documentation from sessionStorage
 * @returns {JSONObject} Full documentation in defined language from sessionStorage
 */
function getLanguageFromSessionStorage() {
    var lang;

    if (sessionStorage.getItem("language") === null) {
        readLanguage();
    }
    lang = sessionStorage.getItem("language");
    lang = JSON.parse(lang);
    return lang;
}