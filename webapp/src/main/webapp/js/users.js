/*
 * USER WINDOW
 */

var countryDeleteHandelr = function(id,el) {
	el.find('#countriesTable tr#tr_'+id).remove();
	var countries = el.find('#ccountries').val();
	var countriesArr = countries.split(',');
	countriesArr.splice( countriesArr.indexOf(''+id), 1 );
	el.find('#ccountries').val(countriesArr.join(","));
	if(countriesArr.length == 0){
		enableCountry(el);
	}
}

var enableCountry = function(el){
	var addCountryBtn = el.find('#addCountryBtn');
	var countriesField = el.find('#countries');
	addCountryBtn.on('click');
	addCountryBtn.click(function() {
		addCountryHandler(el);
	});
	addCountryBtn.removeClass('disabled');
	countriesField.removeAttr('disabled');
}

var createCountryRow = function(id,name,iso3,el) {
	var row = '<tr id=tr_'+id+'><td style="width: 10px;"><button class="btn btn-mini btn-danger" type="button">x</button></td><td>'+name+'</td><td style="width: 50px;">'+iso3+'</td></tr>'
	el.find('#countriesTable > tbody:last').append(row);
	el.find('#countriesTable tr#tr_'+id).find('button').click(function() {
		countryDeleteHandelr(id,el);
	});
	return row;
}

var addCountryHandler = function(el) {
	var countriesField = el.find('#countries');
	var countriesString = el.find('#ccountries');
	var countriesBlock = el.find('#selectedCountries');
	var roleComboBox = el.find('#roleComboBox');
	var addCountryBtn = el.find('#addCountryBtn');
	var value = countriesField.val();
	//var selectedCountry = $.grep(countriesArr, function(e){ return e.name == value; })[0];
	var selectedCountry = map[value];
	countriesField.val('');
	if (!selectedCountry || countriesString.val().indexOf(selectedCountry.id) !== -1) {
		// country already in list
		// ignore
		return false;
	}
	createCountryRow(selectedCountry.id,selectedCountry.name,selectedCountry.iso3,el);
	// check if this country is already selected
	var countries = countriesString.val();	
	if (countries.length > 0) {
		countries += ',' + selectedCountry.id;
	} else {
		countries = selectedCountry.id;
	}
	countriesString.val(countries);
	var role = roleComboBox.val();
	if (role !== 'reviewer' && role !== 'editor') {
		addCountryBtn.off('click');
		addCountryBtn.addClass('disabled');
		countriesField.attr('disabled', 'disabled');
	}
	return false;

};

function changeRole(el){
	var roleComboBox = el.find('#roleComboBox');
	var countriesString = el.find('#ccountries');
	var countriesBlock = el.find('#selectedCountries');
	var addCountryBtn = el.find('#addCountryBtn');
	var countriesField = el.find('#countries');
	var questionsBlock =  el.find('#questions');
	countriesField.val("");
	countriesString.val("");
	el.find('#countriesTable > tbody:last').empty();
	var role = roleComboBox.val();
	var countries = countriesString.val();
	if (role != 'reviewer' && role != 'editor' && countries) {
		addCountryBtn.off('click');
		addCountryBtn.addClass('disabled');
		countriesField.attr('disabled', 'disabled');
	}else{
		enableCountry(el);
	}
	if (role == 'reviewer') {
		questionsBlock.attr('class', 'show');
	} else {
		questionsBlock.attr('class', 'hide');
	}
	if(role == ''){
		addCountryBtn.off('click');
		addCountryBtn.addClass('disabled');
		countriesField.attr('disabled', 'disabled');
		countriesField.val("Chose a role before");
	}
}	
	
function initEditUserWindow(el){
	initShowAll(el);
	initCountrySelector(el);
	var roleComboBox = el.find('#roleComboBox');
	var usernameField = el.find('#cusername');
	var addCountryBtn = el.find('#addCountryBtn');
	var countriesField = el.find('#countries');
	var questionsBlock =  el.find('#questions');
	usernameField.attr('disabled', 'true');
	roleComboBox.attr('disabled', 'true');
	var role = roleComboBox.val();
	if (role != 'reviewer' && role != 'editor') {
		addCountryBtn.off('click');
		addCountryBtn.addClass('disabled');
		countriesField.attr('disabled', 'disabled');
	}else{
		addCountryBtn.on('click');
		addCountryBtn.click(function() {
			addCountryHandler(el);
		});
		addCountryBtn.removeClass('disabled');
		countriesField.removeAttr('disabled');
	}
	if (role == 'reviewer') {
		questionsBlock.attr('class', 'show');
	} else {
		questionsBlock.attr('class', 'hide');
	}
	
	el.find('#countriesTable').find("tr").each(function() {
		  var id = $(this).attr('id').replace("tr_","");
		  $(this).find('button').click(function() {
				countryDeleteHandelr(id,el);
		  });
	});
}

function initCreateUserWindow(el){
	initShowAll(el);
	initCountrySelector(el);
	el.find('#countries').val("Chose a role before");
	var roleComboBox = el.find('#roleComboBox');
	roleComboBox.change(function() {
		changeRole(el);
	});
}

function initShowAll(el){
	/*
	el.find('#showAll').click(function(){
	        //add something to ensure the menu will be shown
		el.find('#countries').val(' ');
		el.find('#countries').typeahead('lookup');	
	});
	*/
}

function initCountrySelector(el){
	el.find('#countries').typeahead({
	    source: function (query, process) {
	    	var result  = [];	 
	    	map = {};
	        $.each(countriesArr, function (i, country) {
	        	var label = country.name + " (" + country.iso3 + ")";
	        	result.push(label);
	        	map[label] = country;
	        });
	        process(result);
	    }
	});
}

function initFilterWindow(el){
	el.find('#filter_countries').typeahead({
	    source: function (query, process) {
	    	var result  = [];	 
	    	map = {};
	        $.each(countriesArr, function (i, country) {
	        	var label = country.name + " (" + country.iso3 + ")";
	        	result.push(label);
	        	map[label] = country;
	        });
	        process(result);
	    },
	    updater: function(item) {
	    	el.find('#countries').val(map[item].id);
	        return item;
	    }
	});
	var selectedId = el.find('#countries').val();
	if(selectedId){
		var filterCountriesValue = $.grep(countriesArr, function(e){ return e.id == selectedId; })[0];
		var label = filterCountriesValue.name + " (" + filterCountriesValue.iso3 + ")";
		el.find('#filter_countries').val(label);
	}
	el.find('#filter_countries').focus(function (e) {
		el.find('#filter_countries').val('');
		el.find('#selCountries').val(''); // Clear typeahead
	});
	el.find('#filter_cname_clear_btn').click(function(event){ el.find('#filter_cname').val('') });
	el.find('#filter_cusername_clear_btn').click(function(event){ el.find('#filter_cusername').val('') });
	el.find('#filter_cemail_clear_btn').click(function(event){ el.find('#filter_cemail').val('') });
	el.find('#filter_roleComboBox_clear_btn').click(function(event){ el.find('#filter_roleComboBox').val('') });
	el.find('#filter_countries_clear_btn').click(function(event){ el.find('#filter_countries').val(''); el.find('#selCountries').val('') });
}

function saveUser(el){
	var cquestions = "";
	el.find(".questionCheck").each(function( index ) {
		if($(this).is(':checked')){
			if(cquestions == ""){
				cquestions = $(this).attr('id');
			}else{
				cquestions = cquestions + "," + $(this).attr('id');
			}
		}
	});
	el.find("#questionsStr").val(cquestions);
	
	el.find("#createUserForm").validate({ignore: ""});

	el.find('#createUserForm').submit();
}

function initActivityLog(){
	var el = $('#filterActivityLogForm');
	el.find('#filter_from_clear_btn').click(function(event){ el.find('#fromDate').val('');el.submit() });
	el.find('#filter_to_clear_btn').click(function(event){ el.find('#toDate').val('');el.submit() });
	el.find('#filter_username_clear_btn').click(function(event){ el.find('#username').val('');el.submit() });
	el.find('#filter_country_clear_btn').click(function(event){ el.find('#country').val('');el.submit( )});
	el.find('#filter_questionId_clear_btn').click(function(event){ el.find('#questionId').val('');el.submit( )});
	el.find('#filter_content_clear_btn').click(function(event){ el.find('#content').val('');el.submit()});
	el.find('#filter_from_clear_btn').click(function(event){ el.find('#fromDate').val('');el.submit()});
	el.find('.icon-filter').click(function(event){el.submit()});
	el.find('#questionId').numeric();
}

$(function(){
	$('#createUserWindow').on('hidden', function() {
		$(this).data('modal').$element.removeData();
	});	
	
	$('#editUserWindow').on('hidden', function() {
		$(this).data('modal').$element.removeData();
	});	

	$('#saveBtn').on('click', function(){
		var el = $('#createUserWindow');
		saveUser(el);
	});
	
	$('#updateBtn').on('click', function(){
		var el = $('#editUserWindow');
		saveUser(el);
	});

	$('#filterBtn').on('click', function() {
		$('#filterUserForm').submit();
	});
	
	$('#createUserWindow').on('loaded',function() {
		var el = $('#createUserWindow');
		initCreateUserWindow(el);
	});
	
	$('#editUserWindow').on('loaded',function() {
		var el = $('#editUserWindow');
		initEditUserWindow(el);
	});
	
	$('#filterWindow').on('loaded',function() {
		var el = $('#filterWindow');
		initFilterWindow(el);
	});

	$('#deleteBtn').on('click', function() {
		var userId = $('#deleteWarningWindow').data('modal').options.userid;
		var page = $('#deleteWarningWindow').data('modal').options.page;
		location.href=contextPath+"/users/delete/"+userId+"/"+page;
	});

	$('#datetimepickerFrom').datetimepicker({
		language: 'pt-BR'
	});
	
	$('#datetimepickerTo').datetimepicker({
		language: 'pt-BR'
	});
	
	initActivityLog();
});
