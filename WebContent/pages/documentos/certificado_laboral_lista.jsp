<%@ include file="/taglibs.jsp"%>
<script type="text/javascript">
	$.tablesorter.addParser({ 
	    id: 'datetime', 
	    is: function(s) { 
	        return false; 
	    }, 
	    format: function(s) { 
	    	if (s.length > 0) {
	    		return $.tablesorter.formatFloat(Date.parseExact(s,'dd/MM/yyyy H:mm').getTime());
	    	} else {
	    	    return 0;
	    	}
	    }, 
	    type: 'numeric' 
	});
	
	$(document).ready(function() {
		
		
		$("#fplanilla").submit(function() {
			
			
				
			 $('#btnGenerarCertificado').attr('disabled','disabled');
			
			
			//actualizar();
			// window.location.reload(true);
			 
		return true; 
			
		});
		
		$("[name=lnkUpdateUsuarioDetalle]").click(function() {
		
			actualizar();
			
		});
		
		$("#dmMensajeCertificado").dialog({   				
			width: 400,
			height: 200,   				
			modal: true,
			autoOpen: false,
			resizable: true
		});
		
		$("[name=btnGenerarCertificado]").button();
		
		$("#contratos").tablesorter({ 
			debug: false,
	      
	        cssHeader: "headerSort",
	        cssAsc: "headerSortUp",
	        cssDesc: "headerSortDown",
	        sortMultiSortKey: "shiftKey"
	    });

		$("#contratos").fixedtableheader();
		
		
		
	});

	/* function generarcertificado(idempleado) {		
		if(idempleado==''){
			
		}else{
		
				$("[name=btnGenerarCertificado]").attr('disabled',true);;
				$("#dmMensajeCertificado").dialog("open");
				$("#dmMensajeCertificado").html(getHTMLLoaging16(' Activando'));
				$.ajax({
					cache: false,
					contentType: 'application/x-www-form-urlencoded; charset=iso-8859-1;', 
		            type: 'post',            
		            url: '${ctx}/page/documentos?action=generar_certificado&idempleado='+idempleado,
		            data: '',
		            dataType: "text",
		            error: function(jqXHR, textStatus, errorThrown) {
		            	$("[name=btnGenerarCertificado]").attr('disabled',false);        		
		                $("#dmMensajeCertificado").html(jqXHR.statusText);
			        },
		            success: function(data) {
		            	  		$("#dmMensajeCertificado").dialog("close");
		               
		               
		            }
		        });	 
			
		}
	} */
function actualizar(){
		$("#dmgenerarcerti").html(getHTMLLoaging30()); 
		$.ajax({
		     contentType: 'application/x-www-form-urlencoded; charset=iso-8859-1;', 
            type: 'post',            
            url: '${ctx}/page/documentos?action=certificado_principal&idempleado=${contrato.idempleado}',
            data: '',
            dataType: "text",
            error: function(jqXHR, textStatus, errorThrown) {
            	       		
                $("#dmMensajeCertificado").html(jqXHR.statusText);
	        },
            success: function(data) {
            	  		$("#dmMensajeCertificado").dialog("close");
            	  		$("#dmgenerarcerti").html(data);
               
            }
				
			});
	}

	
</script>


<div align="center" id="dmgenerarcerti" >
<form name="fplanilla" id="fplanilla"  action="${ctx}/page/documentos"  method="post"  >
	  <input type="hidden" name="action" value="generar_certificado"/>


	<input type="hidden" name="idempleado" value="${contrato.idempleado}">
<%-- <c:choose>
		<c:when test="${fn:length(contratos) eq 0}">
			<div class="msgInfo1" align="left">No se encontraron contratos.</div>
		</c:when>		
		<c:otherwise>
		<fieldset style="width: auto"><legend class="e6" >Certificado laboral</legend>	
			<div>
			<div id="usuario_acciones" style="padding: 5px 0px 0px 0px;">			
			<b>Ventas selecionadas:</b>&nbsp;&nbsp;&nbsp;<span class="enlace" id="enviar_correo_usuario">enviar por correo</span>
			
			<div style="float: right;"><span class="texto1">Ordenar m�ltiples columnas: (Shift+[clic columna])</span></div>
			</div>
			<div style="clear: both;"></div>
			<div id="divcontratos" >
			<table style="width:100%" border="0" id="contratos" class="tExcel tRowSelect">
			
			
			  <thead>
			  <tr class="td3">
			    <th>#</th>			    
				<th><span title="Contrato n�mero">Contrato n�mero</span></th>
				<th><span title="Cargo">Cargo</span></th>
				<th><span title="Fecha inicial">Fecha Inicial</span></th>
				<th><span title="Fecha final">Fecha final</span></th>	
			  	<th><span title="Tipo de contrato">Tipo de contrato</span></th>
			  				  				  	
			  	<th>Salario Mensual</th>
			  	<th>Usuario crea</th>
			  	<th>Fechacrea crea</th>
			  	<th>Usuario mod</th>
			  	<th>Fecha mod</th>
			  </tr>
			  </thead>
			  <tbody>
			  <c:forEach items="${contratos}" var="contrato" varStatus="loop">
			  <tr style="color: ${contrato.estado==2?'':'red'};">
			   <tr>
			  	<td><c:out value="${loop.index+1}"/></td>			  		
			  	<td><c:out value="${contrato.numerocontrato}"/></td>
			  	<td><c:out value="${contrato.cargo.nombrecargo}"/></td>
			  	<td><c:out value="${contrato.fechainicio}"/></td>  
			 	<td><c:out value="${contrato.fechafin}"/></td>	
			  	<td><c:out value="${contrato.contratotipo.nombrecontratotipo}"/></td>	
			 	<td><c:out value="${contrato.salario}"/></td>
			  	<td><c:out value="${contrato.idusuariocrea} "/></td>
			 	<td><fmt:formatDate value="${contrato.fechacrea}" pattern="dd/MM/yyyy H:mm"/></td>	
			  	<td><c:out value="${contrato.idusuariomod} "/></td>
			  	<td><fmt:formatDate value="${contrato.fechamod}" pattern="dd/MM/yyyy H:mm"/></td>			  		  	
			  	<td valign="middle" align="center"><span class="enlace" onclick="visualizarDetalleEmpleado(${contrato.idcontrato});" title="Detalle"><img alt="Detalle" src="${ctx}/imagen/ico-editar.gif"></span></td>	
			  
			 </tr>			 
			  </c:forEach>			  
			  </tbody>
			</table>
			</div>
			</div>
			</fieldset>
	</c:otherwise>
</c:choose>  --%>

<div align="center">
<fieldset style="width: auto"><legend class="e6" >Certificado laboral</legend>	
	<c:if test="${sessionScope[consHermes.USUARIO_SESSION].usuarioaplicacion.idgrupo == 1 || sessionScope[consHermes.USUARIO_SESSION].usuarioaplicacion.idgrupo == 214 || sessionScope[consHermes.USUARIO_SESSION].usuarioaplicacion.idgrupo == 235}">
		 <span class="enlace" id='lnkUpdateUsuarioDetalle' name='lnkUpdateUsuarioDetalle'>Actualizar</span>&nbsp;&nbsp;&nbsp;
				<c:if test="${mensaje=='No tiene:'}">
				<div class="texto1">1. La contrase�a para abrir el certificado es su n�mero de cedula.</div> 
				<br>
				<div class="texto1">2. Recuerde que solo puede generar un m�ximo de  cinco(5) certificados por mes.</div>
				<br> 
				<div class="msgInfo1" align="center">${mensaje1}</div>
				<br>
				
					<c:if test="${boton==1}">
					<button type="submit" onsubmit="this.disabled=true;actualizar();" id="btnGenerarCertificado" name="btnGenerarCertificado" >Generar certificado</button>&nbsp;&nbsp;&nbsp;
					
					</c:if>
					
				</c:if>
				<c:if test="${mensaje!='No tiene:' && fn:length(mensaje1)>2}">
				
					<div class="msgInfo1" align="center">${mensaje}</div>
					<br>
					<div class="msgInfo1" align="center">${mensaje1}</div>
					<br>
				</c:if>
				<div class="texto1"> Para gestionar la recuperaci�n de su contrase�a, debe conocer las respuestas a las preguntas de seguridad  que eligi�, esta informaci�n la puede actualizar desde el link  "cambiar contrase�a", que se encuentra  en la parte superior derecha, en la pesta�a "preguntas de seguridad".
						Cuando olvide su clave, en la p�gina de ingreso en la parte inferior encontrara el link "recordar contrase�a" para iniciar el proceso de recuperaci�n.
				</div>
	</c:if>
</fieldset>
</div>
</form>
</div>

<div id="dmMensajeCertificado" title="Mensaje"></div>