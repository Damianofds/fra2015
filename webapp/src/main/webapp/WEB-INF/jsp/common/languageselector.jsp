<select id="languageSelector" class="language input-medium" style="width:100px"ONCHANGE="location = '?lang='+this.options[this.selectedIndex].value;">
	<option value="en" ${pageContext.response.locale=='en'?'selected':''}>English</option>
	<option value="fr" ${pageContext.response.locale=='fr'?'selected':''}>Fran�ais</option>
	<option value="es" ${pageContext.response.locale=='es'?'selected':''}>Espa�ol</option>
</select>