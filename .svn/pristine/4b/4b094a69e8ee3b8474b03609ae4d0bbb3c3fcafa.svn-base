<%@ include file="/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    	               "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title><c:if test="${applicationScope[consHermes.APLICACION_SESSION].nombreexterno!=''}"><c:out value="${applicationScope[consHermes.APLICACION_SESSION].nombreexterno}" escapeXml="true"/> - </c:if>Sistemcobro</title>
	
	







<script type="text/javascript">

	
	$(document).ready(function() {
		

	
	  

	
		
		$("#tabsOpcionesdocumentos").tabs({
			
			cache: true,
			spinner: ' '+getHTMLLoaging14(''),			
			ajaxOptions: {
				cache: false,
				error: function( xhr, status, index, anchor ) {
					$( anchor.hash ).html(
						"No se pudo cargar esta pestaña. Informe a su área de tecnología." );
				}
			}
		})  .addClass('ui-tabs-vertical ui-helper-clearfix');
		

		$('#tabsOpcionesdocumentos')
	    .tabs()
	    .addClass('ui-tabs-vertical ui-helper-clearfix');
	


	    
	});

	function recargarTabOpcion(index){
		$("#tabsOpcionesdocumentos").tabs("load", index );
	}
	
	
	
	
	
</script>
</head>
<body>

<div id="tabsOpcionesdocumentos" class="tab tab-vert">
	<ul>
			<%--  <c:if test="${sessionScope[consHermes.USUARIO_SESSION].usuarioaplicacion.idgrupo == 1 || sessionScope[consHermes.USUARIO_SESSION].usuarioaplicacion.idgrupo == 212 || sessionScope[consHermes.USUARIO_SESSION].usuarioaplicacion.idgrupo == 213 || sessionScope[consHermes.USUARIO_SESSION].usuarioaplicacion.idgrupo == 214 || sessionScope[consHermes.USUARIO_SESSION].usuarioaplicacion.idgrupo == 215}">
		</c:if>	 --%>
		<li><a href="${ctx}/page/documentos?action=certificado_principal">&nbsp;Certificados<span>&nbsp;</span></a></li>	
		
	
		
		<%--  <c:if test="${sessionScope[consHermes.USUARIO_SESSION].usuarioaplicacion.idgrupo == 1 || sessionScope[consHermes.USUARIO_SESSION].usuarioaplicacion.idgrupo == 212 || sessionScope[consHermes.USUARIO_SESSION].usuarioaplicacion.idgrupo == 213 || sessionScope[consHermes.USUARIO_SESSION].usuarioaplicacion.idgrupo == 214 || sessionScope[consHermes.USUARIO_SESSION].usuarioaplicacion.idgrupo == 215}">
	 		<li><a href="${ctx}/page/administracion?action=administracion_principal">&nbsp;Prueba<span>&nbsp;</span></a></li>
		</c:if>		
		 --%>
	</ul>
</div>

<div style="padding: 50px 0px;" ></div>

</body>
</html>
