/*
 * Copyright 2013 Michael Ruster.
 *
 * This file is part of Polsearchine.
 *
 * Polsearchine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polsearchine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Polsearchine. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Returns if source is known to system for querying.
 * @param {string} source Chooses between source to search (e.g. image or web).
 */
function isKnownSource(source) {
	/** @const */ var KNOWN_SOURCES = ["web", "image"];
	return ($.inArray(source, KNOWN_SOURCES) !== -1);
}

/**
 * Initialises tooltips for the navigation buttons. Initialises
 * variables and uses the entered searchTerm to retrieve and display
 * results. Displays a placeholder to inform the user whenever content
 * is being loaded.
 */
function initialiseTooltips() {
	$('#searchPagesNavbutton').tooltip();
	$('#searchImagesNavbutton').tooltip();
}

/**
 * This function passes the search term to the FileTypeServlet and appends
 * the returned data to webpage if necesseary.
 * @param {string} searchTerm which is the term to analyse.
 */
function checkForFileTypeSearch(searchTerm) {
	$.ajax({
		url: "/FileTypeServlet",
		type: "POST",
		async: true,
		data: {
			searchTerm: searchTerm
		}
	}).success(function(recommendation) {
		if (recommendation) {
			$("#fileTypeMessage").append(recommendation);
		}
	});
}

/**
 * Initialisation method that is run once. It stores the search term and
 * calculates the new skip value from the first search results. It also binds
 * the scroll method for dynamic reloading of content.
 * It loads the API provider's banner and puts up the file type-message if
 * necessary (only available for 'web').
 * @param {string} source Chooses between source to search (e.g. image or web).
 * @param {number} RESULTS_AMOUNT This is the amount of results to fetch.
 */
function monitorScrollingForReloading(source, RESULTS_AMOUNT) {
	var searchTerm = $("#currentSearchTerm").val();
	insertResultsProviderBanner();
	if (source === "web") {
		if (searchTerm.indexOf("filetype:") === -1) {
			checkForFileTypeSearch(searchTerm);
		}
	}
	var skip = loadResults(source, true, searchTerm, RESULTS_AMOUNT, 0);
	$(window).scroll(function() {
		if ($(window).scrollTop() + $(window).height() > $(document).height() - 200) {
			skip = loadResults(source, false, searchTerm, RESULTS_AMOUNT, skip);
		}
	});
}

/**
 * Fetches result and appends them to #sourceResults (where source is the
 * parameter). If there are no more results to deliver (determined by finding an
 * element with id="endOfResults") the scrolling function will be unbound. The
 * loading notification will be shown throughout the process.
 * @param {string} source Chooses between source to search (e.g. image or web).
 * @param {boolean} async Determines whether the request is asynchronous.
 * @param {string} searchTerm This is the input value to search for.
 * @param {number} amount This is the amount of results to fetch.
 * @param {number} skip This is the amount of results to skip.
 * @return {number} Skip value for next loading or empty string if source was
 *                       unknown.
 */
function loadResults(source, async, searchTerm, amount, skip) {
	var placeholder = $("#resultsPlaceholder").get(0);
	placeholder.style.display = "inline-block";
	if (isKnownSource(source)) {
		$.ajax({
			url: "/ResultsServlet",
			type: "POST",
			async: async,
			data: {
				searchTerm: searchTerm,
				source: source,
				top: amount,
				skip: skip
			}
		}).success(function(results) {
			$("#" + source + "Results").append(results);
			if ($("#endOfResults").length !== 0) {
				$(window).unbind("scroll");
				placeholder.style.display = "none";
			}
		});
		return (skip + amount);
	}
	return ""; // don't let the user know
}

/**
 * Sets a button to navigation button to active for styling.
 * @param {string} source Chooses between source to search (e.g. image or web).
 */
function setActiveNavButton(source) {
	if (isKnownSource(source)) {
		var navButton = $("#" + source + "NavButton").get(0);
		navButton.className = "active";
	}
}

/**
 * Loads results provider banner and inserts it into a DOM-element.
 */
function insertResultsProviderBanner() {
	$.ajax({
		url: "/ResultsProviderBannerServlet",
		type: "GET",
		async: true
	}).success(function(results) {
		$("#resultsProvider").get(0).innerHTML = results;
	});
}

/**
 * Loads search engine legal text and inserts it into a DOM-element.
 */
function insertSearchEngineLegalText() {
	$.ajax({
		url: "/SearchEngineLegalTextServlet",
		type: "GET",
		async: true
	}).success(function(results) {
		$("#searchEngineLegalText").get(0).innerHTML = results;
	});
}

/**
 * This function focusses and selects the object o.
 * @param {object} o
 */
function selectThis(o) {
	o.focus();
	o.select();
}

/**
 * This function toggles the visibilty state of the regulated result's
 * information.
 * @param {string} idNumber is the number of the result's dl-element to toggle.
 */
function toggleRegulationBanner(idNumber) {
	var resultId = ".regulatedResult_" + idNumber;
	$(resultId).toggle();
}
