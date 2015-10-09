package co.sistemcobro.intranet.web;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.net.util.Base64;
import org.apache.log4j.Logger;

import co.sistemcobro.all.constante.EstadoEnum;
import co.sistemcobro.all.exception.DatoException;
import co.sistemcobro.all.exception.LogicaException;
import co.sistemcobro.all.util.Util;
import co.sistemcobro.hermes.bean.AplicacionBean;
import co.sistemcobro.hermes.bean.UsuarioBean;
import co.sistemcobro.hermes.constante.AplicacionEnum;
import co.sistemcobro.hermes.constante.Constante;
import co.sistemcobro.hermes.ejb.UsuarioEJB;
import co.sistemcobro.intranet.bean.ContratoReporte;
import co.sistemcobro.intranet.bean.ParametrosparaReporte;
import co.sistemcobro.intranet.util.*;
import co.sistemcobro.rrhh.bean.Contrato;
import co.sistemcobro.rrhh.bean.DocumentoConfiguracion;
import co.sistemcobro.rrhh.bean.EmpleadoBean;
import co.sistemcobro.rrhh.bean.EmpleadoDocumentoGenerado;
import co.sistemcobro.rrhh.bean.EmpleadoSalario;
import co.sistemcobro.rrhh.constante.DocumentoTipoEnum;
import co.sistemcobro.rrhh.ejb.ContratoEJB;
import co.sistemcobro.rrhh.ejb.EmpleadoEJB;
import co.sistemcobro.intranet.util.FtpUtilCliente;
import co.sistemcobro.rrhh.util.MergePDF;

/**
 * 
 * @authorLeonardotalero
 * 
 */
@WebServlet(name = "DocumentosServlet", urlPatterns = { "/page/documentos" })
public class DocumentosServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(DocumentosServlet.class);

	@EJB
	private UsuarioEJB usuarioEJB;

	@EJB
	private EmpleadoEJB empleadoEJB;
	@EJB
	private ContratoEJB contratoEJB;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DocumentosServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		action = action == null ? "" : action;
		try {

			if (action.equals("certificado_principal")) {
				certificado_principal(request, response);
			} else if (action.equals("documentos_principal")) {
				documentos_principal(request, response);
			} else if (action.equals("generar_certificado")) {
				generar_certificado(request, response);
			}

		} catch (Exception e) {
			logger.error(e.toString(), e.fillInStackTrace());
		}
	}

	public void documentos_principal(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		UsuarioBean user = (UsuarioBean) session.getAttribute(Constante.USUARIO_SESSION);
		try {
			UsuarioBean usuario = usuarioEJB.getUsuario(user.getIdusuario());
		String mensaje="";
			List<EmpleadoBean> empleados = empleadoEJB.buscarEmpleadosporCodigoIdentificacion(usuario.getCodigoidentificacion());
			if(empleados!=null && empleados.size()>0){
				
				request.setAttribute("mensaje", mensaje);
				request.getRequestDispatcher("../pages/documentos/documentos_principal.jsp").forward(request, response);
			
			}
			} catch (Exception e) {
			logger.error(e.toString(), e.fillInStackTrace());
			response.sendError(1, e.getMessage());
		}

	}

	public void certificado_principal(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		UsuarioBean user = (UsuarioBean) session.getAttribute(Constante.USUARIO_SESSION);
		try {
			UsuarioBean usuario = usuarioEJB.getUsuario(user.getIdusuario());
		
			List<EmpleadoBean> empleados = empleadoEJB.buscarEmpleadosporCodigoIdentificacion(usuario.getCodigoidentificacion());
			if(empleados!=null && empleados.size()>0){
				
				 List<Contrato> contratos = contratoEJB.getContratosporIdEmpleado(empleados.get(0).getIdempleado());
				//String idempleado = request.getParameter("idempleado");
				//request.setAttribute("empleado", empleadoEJB.buscarEmpleadosporId(Long.valueOf(idempleado)));
				//request.setAttribute("empresas", contratoEJB.getEmpresas() );
				 Contrato contrato = contratoEJB.getUltimoContratosporIdEmpleado(empleados.get(0).getIdempleado());
					request.setAttribute("contrato", contrato);
					
					String mensaje="No tiene:";
					String mensaje1="";
					if(contratos.size()>0){
						//Contrato ultimocontrato = contratos.get(0);
						Contrato ultimocontrato=contrato;
						EmpleadoSalario ultimosalario = contratoEJB.obtenerEmpleadoSalarioPorIdContrato(ultimocontrato.getIdcontrato());
						if(ultimocontrato.getNumerocontrato()==null || ultimocontrato.getNumerocontrato().equals("") ){
							mensaje+=" número de contrato.";
						}if(ultimocontrato.getIdcontratotipo()==null || ultimocontrato.getIdcontratotipo()==0 ){
							mensaje+=" tipo de contrato.";
						}if(ultimosalario==null || ultimosalario.getIdempleadosalario()==null){
							mensaje+=" salario asignado.";
						}
					if(ultimocontrato.getIdcargo()==null){
							mensaje+=" cargo asignado.";
						}
						
					}
				
					List<EmpleadoDocumentoGenerado> certificados = empleadoEJB.buscarCertificadosGeneradosporEmpleadoyfecha(empleados.get(0).getIdempleado(),new Date());
					int boton=0;
					if(certificados!=null && certificados.size()<=5){
						mensaje1+=" Certificados generados este mes "+certificados.size();
						boton=1;
					}else{
						
					}
					if(certificados!=null && certificados.size()>=6){
						mensaje1+=" Ya completo el número de certificados permitidos para  este mes  "+certificados.size();
					boton=0;
					}else{
						
					}
					
					
					//request.setAttribute("certificados", certificados);
					request.setAttribute("mensaje", mensaje);
					request.setAttribute("boton", boton);
					request.setAttribute("mensaje1", mensaje1);
					
				 request.setAttribute("contratos", contratos);
			
				request.getRequestDispatcher("../pages/documentos/certificado_laboral_lista.jsp").forward(request, response);
			
			}
			} catch (Exception e) {
			logger.error(e.toString(), e.fillInStackTrace());
			response.sendError(1, e.getMessage());
		}

	}
	
	@SuppressWarnings("unused")
	public void generar_certificado(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		UsuarioBean user = (UsuarioBean) session.getAttribute(Constante.USUARIO_SESSION);
		EmpleadoDocumentoGenerado empleadodocumentogenerado=new EmpleadoDocumentoGenerado();
		try {
			UsuarioBean usuario = usuarioEJB.getUsuario(user.getIdusuario());
		
			List<EmpleadoBean> empleadoss = empleadoEJB.buscarEmpleadosporCodigoIdentificacion(usuario.getCodigoidentificacion());
			
		
				
				 EmpleadoBean empleados = empleadoEJB.buscarEmpleadosporId(Long.valueOf(empleadoss.get(0).getIdempleado()));
					UsuarioBean usuariot = usuarioEJB.getUsuarioPorNumIdentificacion(empleados.getEmpleadoidentificacion().getNumeroidentificacion());
					if(empleados!=null){
						ParametrosparaReporte parametros=new ParametrosparaReporte();
						EmpleadoBean empleado =empleados;
						List<Contrato> contratos = contratoEJB.getContratosporIdEmpleado(empleados.getIdempleado());
						Contrato contrato = contratoEJB.getUltimoContratosporIdEmpleado(empleados.getIdempleado());
					
						List<ContratoReporte> contratosreporte=new ArrayList<ContratoReporte>();
						List<ContratoReporte> contratosreportemasreciente=new ArrayList<ContratoReporte>();
						ContratoReporte contratoreporte = new ContratoReporte();
						String temp="";
						String moneda="";
					 for(Contrato c:contratos){
						
						 logger.info("contrato. "+c.getIdcontrato());
						EmpleadoSalario salario = contratoEJB.obtenerEmpleadoSalarioPorIdContrato(c.getIdcontrato());
						
						if(salario!=null){
							c.setSalario(salario.getSalario());
							//moneda=salario.getTipomoneda().getSimbolo()+" "+salario.getTipomoneda().getCodigoiso();
						}else
						{
							c.setSalario("0");
						}
						
						 HashMap<String, Object> resu = verificasitienemismonumercontrato(contratosreporte, c.getNumerocontrato());
						 Boolean valorboleano = (Boolean)resu.get("boolean");
						 if(valorboleano){
							 
							 Integer orden = (Integer)resu.get("orden");
							 ContratoReporte contratoreporterepetido = contratosreporte.get(orden);
							 if(c.getFechainicio().before(contratoreporterepetido.getFechainicio())){
								 contratoreporterepetido.setFechainicio(c.getFechainicio());
							 }
							 
							 if(c.getFechafin()==null){
								 contratoreporterepetido.setFechafinstring("ACTUALMENTE");
							 }else{
	 
								 if(contratoreporterepetido.getFechafin()!=null ){
									 if(c.getFechafin().after(contratoreporterepetido.getFechafin())){
										 contratoreporterepetido.setFechafinstring(c.getFechafin().toString());
									 } 
								 }else{
									
									 
								 }
							 }
							 
							 
							 
							 
							
						 }else{
							 contratoreporte=new ContratoReporte();
							 contratoreporte.setUltimocargo(c.getCargo().getNombrecargo());
							 contratoreporte.setFechainicio(c.getFechainicio());
							 contratoreporte.setIdcontrato(c.getIdcontrato());
							 if(c.getFechafin()!=null){
									contratoreporte.setFechafin(c.getFechafin());
									contratoreporte.setFechafinstring(Util.timestampToString(c.getFechafin(), "dd/MM/yyyy"));
							 }else{
								 contratoreporte.setFechafinstring("ACTUALMENTE");
							 }
							
							 contratoreporte.setNumerocontrato(c.getNumerocontrato());
							 if(c.getSalario()==null || c.getSalario().equals("")){
								 contratoreporte.setUltimosalario("0");
								 contratoreporte.setUltimosalarionumero(Double.valueOf(0));
								 moneda="";
									
							 }else{
								 contratoreporte.setUltimosalario(c.getSalario());
								 contratoreporte.setUltimosalarionumero(Double.valueOf(c.getSalario()));
								 if(salario!=null && salario.getTipomoneda()!=null){
									 moneda=salario.getTipomoneda().getSimbolo()+" "+salario.getTipomoneda().getCodigoiso();
										 
								 }else{
									moneda=""; 
								 }
								 contratoreporte.setMoneda(moneda);
							 }
							
							 
							 contratosreporte.add(contratoreporte);
							 
						 }
						
						 temp = c.getNumerocontrato();
					 }
					 
					 contratosreportemasreciente.add(contratosreporte.get(0));
					parametros.setContratosreporte(contratosreportemasreciente);
					
					
					List<DocumentoConfiguracion> documentoconfiguracion = empleadoEJB.getDocumentoConfiguracion(Long.valueOf(DocumentoTipoEnum.CERTIFICADO_LABORAL.getIndex()));
					parametros=setparametrosreporte(documentoconfiguracion,parametros);
					
					String titulosalario = parametros.getTitulosalario();
					titulosalario+=" "+contratosreporte.get(0).getMoneda();
					parametros.setTitulosalario(titulosalario);
					
					if(usuario!=null){
						parametros.setNombrepersona(usuario.getNombre());
						parametros.setNombrepersona(empleado.getNombres()+" " +empleado.getApellidos());
						
					}else{
						parametros.setNombrepersona(empleado.getNombres()+" " +empleado.getApellidos());
					}
				
					parametros.setTipodocumento(empleado.getIdentificaciontipo().getTipo());
					parametros.setCedulapersona(empleado.getEmpleadoidentificacion().getNumeroidentificacion());
					parametros.setCiudadexpedicion(empleado.getEmpleadoidentificacion().getCiudadexpedicion());
					logger.info("antes de contrato. ");
					contrato=contratoEJB.getContratosporId(contratosreportemasreciente.get(0).getIdcontrato());
					logger.info("despues de contrato. ");
					parametros.setTipocontrato(contrato.getContratotipo().getNombrecontratotipo());

					NumeroALetras numLetra= new NumeroALetras();
					Date fechanow=new Date();
					GregorianCalendar grego=new GregorianCalendar();
					grego.setTime(fechanow);
					 int anio = grego.get(Calendar.YEAR);
					 int mes = grego.get(Calendar.MONTH);
					 int dia = grego.get(Calendar.DAY_OF_MONTH);
					
					 String aniostring = numLetra.convertir(String.valueOf(anio),true);
					 String messtring = numLetra.convertir(String.valueOf(mes), true);
					 String messtri = new SimpleDateFormat("MMMM",new Locale("es", "ES")).format(fechanow);
					 String diastring = numLetra.convertir(String.valueOf(dia), true);
					
					 parametros.setCiudadexpedicioncertificado(" Bogotá ");
					 parametros.setAnoenletras(aniostring+"("+anio+")");
					 parametros.setMesenletras(messtri.toUpperCase());
					 parametros.setDiaenletras(diastring+"("+dia+")");
					
					  long s = new Date().getTime();
				        MessageDigest m=MessageDigest.getInstance("MD5");
				        m.update(String.valueOf(s).getBytes(),0,String.valueOf(s).length());
				        String MD5 = new BigInteger(1,m.digest()).toString(16);
				        System.out.println("MD5: "+MD5);
				        byte[] encodedBytes = Base64.encodeBase64(empleado.getEmpleadoidentificacion().getNumeroidentificacion().getBytes());
				        String base64 = new String(encodedBytes);
				        System.out.println("encodedBytes " +base64 );
				        parametros.setCodigoverificacion(MD5+"_"+base64);
				    	
				    	
				        String nombrejasper="CERTIFICADO_LABORAL";
			//////////////inserta registro
						
									empleadodocumentogenerado.setIdempleado(empleado.getIdempleado());
									empleadodocumentogenerado.setIddocumentotipo(Long.valueOf(DocumentoTipoEnum.CERTIFICADO_LABORAL.getIndex()));
									String nombredocumento = nombrejasper+"_"+empleado.getEmpleadoidentificacion().getNumeroidentificacion()+".pdf";
									empleadodocumentogenerado.setNombredocumento(nombredocumento);
									empleadodocumentogenerado.setNombreamostrar(nombredocumento);
									empleadodocumentogenerado.setCodigoverificacion(MD5+"_"+base64);
									logger.info("archivo MD5: "+MD5+"base64:"+base64);
									empleadodocumentogenerado.setIdusuariocrea(user.getCodusuario());
									empleadodocumentogenerado.setEstado(EstadoEnum.ACTIVO.getIndex());
									logger.info("antes de insertar registro");
									empleadodocumentogenerado=empleadoEJB.insertarEmpleadoDocumentogenerado(empleadodocumentogenerado);
									String nombredocumentoasubir = empleadodocumentogenerado.getIdempleadodocumentogenerado().toString()+"_"+ nombredocumento;
									empleadodocumentogenerado.setNombredocumento(nombredocumentoasubir);
									empleadodocumentogenerado.setIdusuariomod(user.getCodusuario());
									 EmpleadoDocumentoGenerado resu = empleadoEJB.actualizarEmpleadoDocumentoGenerado(empleadodocumentogenerado);
									
									resu= empleadoEJB.buscarEmpleadoDocumentoGenerado(resu.getIdempleadodocumentogenerado());
									parametros.setCodigocertificado(resu.getCodigocertificado());
									
									
									
									logger.info("despues de insertar registro");

									HashMap<String, Object> data = new HashMap<String, Object>();
									data.put("nombre", "planilla");
									data.put("Myobjeto", parametros);
									data.put("firma", parametros.getFirma());
									
									data.put("nombrejasper", nombrejasper);
									data.put("listadocontratos", parametros.getContratosreporte());
									data.put("idempleado", contrato.getIdempleado().toString());
									logger.info("antes se generar. ");
							/////despues de insertar
							
							JasperPrint planilla = generarXLSjasper(request, response, data);
							logger.info("despues se generar. ");
							byte[] pdfPlanilla = null;
							byte[] archivoCompleto = null;
							byte[] archivo=null;
							try {
								
								List<InputStream> pdfs = new ArrayList<InputStream>();
								
								pdfPlanilla = JasperExportManager.exportReportToPdf(planilla);
								
								ServletOutputStream outstream = response.getOutputStream();
								logger.info("archivo. "+pdfPlanilla);
								InputStream planillaIs = new ByteArrayInputStream(pdfPlanilla);
								
								pdfs.add(planillaIs);
								logger.info("antes se ecnrypt. "+planillaIs);
								logger.info("identificacion: "+empleado.getEmpleadoidentificacion().getNumeroidentificacion().toString());
								archivo = MergePDF.encryptPdf(planillaIs,empleado.getEmpleadoidentificacion().getNumeroidentificacion().toString());
								logger.info("despues se ecnrypt. bytes "+archivo.length);
								
								
							} catch (JRException e) {
								// TODO Auto-generated catch block
								logger.info("error "+e);
								e.printStackTrace();
							}
							if (archivo != null) {
								logger.info("archivo !=null. ");
							
								ServletOutputStream outstream = response.getOutputStream();
								response.setContentType("application/pdf");
								response.setContentLength(archivo.length);

								String nombrearchivo = nombredocumento;
								// response.setHeader("Content-disposition", "inline; filename=\"" + nombrearchivo + "\"");
								response.addHeader("Content-Disposition", "attachment; filename=\"" + nombrearchivo + "\"");

								outstream.write(archivo);
								outstream.close();
								int flag=0;
								for(int i=0;i<=5;i++){//intenta 3 veces la conexion
									
									
								if(FtpUtilCliente.checkconexion()){
									String filepdf = co.sistemcobro.intranet.constante.Constante.ROOT_FILE_TEMPORARY + File.separator + nombredocumentoasubir;
									File archivopdf = new File(filepdf);
									FileOutputStream ospdf = new FileOutputStream(archivopdf);
									ospdf.write(archivo);
									ospdf.close();
									String ruta="certificados_generados";
									if(FtpUtilCliente.uploadFTP(archivopdf, ruta)){
										logger.warn("sube archivo pdf");
										flag=1;
									}else{
										logger.warn("error subiendo");
									}
									break;
								}
								
								}if(flag==0){
									throw new LogicaException(
											"Error: No fue posible conectarse al FTP ");
								}
								
								
								logger.warn("termina export pdf");
							}else{
								logger.info("archivo null. ");
							}

							
						
						}else{
							logger.info("error"+" no existe en hermes o en rrhh");
							throw new Exception("no existe en hermes o en rrhh");
							//response.sendError(1, "no existe en hermes o en rrhh");
						}
							
						
					} catch (LogicaException e) {
						
						 try {
							 empleadodocumentogenerado.setEstado(EstadoEnum.DESHABILITADO.getIndex());
							EmpleadoDocumentoGenerado resu = empleadoEJB.actualizarEmpleadoDocumentoGenerado(empleadodocumentogenerado);
						} catch (DatoException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (LogicaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
							
						
						logger.info("eror"+e);
						response.sendError(1, e.toString());
					}  catch (Exception e) {
						logger.error(e.toString(), e.fillInStackTrace());
						response.sendError(1, e.getMessage());
					}

				}

	private JasperPrint generarXLSjasper(HttpServletRequest request, HttpServletResponse response, HashMap<String, Object> data) throws ServletException, IOException {

		byte[] bytes = null;
		JasperPrint jp = new JasperPrint();

		ParametrosparaReporte objeto = (ParametrosparaReporte) data.get("Myobjeto");

		// List<Documentodetalle> lista = (List<Documentodetalle>) data.get("lista");
		String nombrelistado = (String) data.get("nombre");
		String nombrejasper = (String) data.get("nombrejasper");
		String idempleado = (String) data.get("idempleado");
		String firma = (String) data.get("firma");
		logger.info("Nombre. "+nombrejasper);
		
		// String nombrejasper=(String) data.get("nombrejasper");
		// String fecha=(String) data.get("fecha");
		// String ciudad=(String) data.get("ciudad");
		// String version=(String) data.get("version");
		// String nombrefirma=(String) data.get("nombrefirma");
		HashMap<String, Object> param = new HashMap<String, Object>();
		JRBeanCollectionDataSource ds = null;
	
		List<ContratoReporte> listado = (List<ContratoReporte>) data.get("listadocontratos");
		ds = new JRBeanCollectionDataSource(listado, false);

		String novalido = (String) data.get("novalido");

		logger.info("listado " + nombrelistado + ": en proceso de generar.");
		try {

			if (null != objeto) {

				String rutaimagenes = request.getRealPath("WEB-INF") + File.separator + "certificados" + File.separator;
				String nombrecodigo="QR_"+new Date().getTime();
				String rutatemporal=co.sistemcobro.intranet.constante.Constante.ROOT_FILE_TEMPORARY+ File.separator+nombrecodigo;
				logger.info("ruta imagenes." +rutaimagenes);
				param.put("Myobjeto", objeto);
				param.put("firma", firma.trim());
				param.put("ruta", rutaimagenes);
				param.put("idempleado", idempleado);
				param.put("logosistemcobro","LogoSistemcobro.png");
				param.put("sistemcobroISO","sistemcobroISO9001.png");
				param.put("rutaQR",rutatemporal+".png");
				String enlaceatributos=objeto.getCodigoverificacion()+"&tipobusqueda=10";
				byte[] encodedBytes = Base64.encodeBase64(enlaceatributos.getBytes());
		        String base64 = new String(encodedBytes);
		        //http://www.sistemcobro.com:8082/intranet/portal;
		        AplicacionBean aplicacion =  usuarioEJB.getAplicacionPorIdaplicacion(AplicacionEnum.INTRANET.getIndex());
		        aplicacion.getLinkpublico();
				Util.generarqrcode(aplicacion.getLinkpublico()+"/portal/certificado?action=buscar_certificado&codigoverificacion="+base64,rutatemporal+".png");
				
				param.put("QRcode",rutatemporal+".png");
				param.put("sistemcobromarcaagua","sistemcobro_marca_agua.png");
				JasperPrint jp1 = new JasperPrint();
				JRDataSource ds1 = ds;
				String fileName = request.getRealPath("WEB-INF") + File.separator + "certificados" + File.separator + nombrejasper + ".jasper";
				logger.info("antes de fillreport"+fileName);
				jp = JasperFillManager.fillReport(fileName, param, ds1);
				logger.info("despues de fillreport");
				bytes = JasperExportManager.exportReportToPdf(jp);


			} else {
				logger.info("No se puede generar informe ");
			}

		} catch (Exception e) {
			logger.error("Atrapó la excepcion", e);
			response.sendError(1, e.getMessage());
		}

		return jp;
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	
	private HashMap<String, Object> verificasitienemismonumercontrato(List<ContratoReporte> contratoreporte,String numerocontato){
		
		HashMap<String, Object> resu = new HashMap<String, Object>();
		int i=0;
		resu.put("boolean", false);
		for(ContratoReporte cr:contratoreporte){
			
			if(numerocontato.equals(cr.getNumerocontrato())){
				resu.put("boolean", true);
				resu.put("orden", i);
				break;
			}else{
				resu.put("boolean", false);
				
			}
			i++;
		}
	
				
		return resu;
		
	}
	
	private ParametrosparaReporte setparametrosreporte(List<DocumentoConfiguracion> documentoconfiguracion,ParametrosparaReporte parametros){
		
		
		for(DocumentoConfiguracion d:documentoconfiguracion){
			if(d.getNombrecampo().equals("titulo")){
				parametros.setTitulo(d.getTexto());
			}else if(d.getNombrecampo().equals("text1")){
				parametros.setTexto1(d.getTexto());
			}
			else if(d.getNombrecampo().equals("text2")){
				parametros.setTexto2(d.getTexto());
			}
			else if(d.getNombrecampo().equals("nombreempresa")){
				parametros.setNombreempresa(d.getTexto());
			}
			else if(d.getNombrecampo().equals("nit")){
				parametros.setNitempresa(d.getTexto());
			}
			else if(d.getNombrecampo().equals("text3")){
				parametros.setTexto3(d.getTexto());
			}
			else if(d.getNombrecampo().equals("text4")){
				parametros.setTexto4(d.getTexto());
			}
			else if(d.getNombrecampo().equals("text5")){
				parametros.setTexto5(d.getTexto());
			}
			else if(d.getNombrecampo().equals("text6")){
				parametros.setTexto6(d.getTexto());
			}
			else if(d.getNombrecampo().equals("text7")){
				parametros.setTexto7(d.getTexto());
			}
			else if(d.getNombrecampo().equals("text8")){
				parametros.setTexto8(d.getTexto());
			}
			else if(d.getNombrecampo().equals("texttitulocargo")){
				parametros.setTitulocargo(d.getTexto());
			}
			else if(d.getNombrecampo().equals("textfechai")){
				parametros.setTitulofechainicio(d.getTexto());
			}
			
			else if(d.getNombrecampo().equals("textfechaf")){
				parametros.setTitulofechafin(d.getTexto());
			}
			else if(d.getNombrecampo().equals("textsalario")){
				parametros.setTitulosalario(d.getTexto());
			}
			else if(d.getNombrecampo().equals("text9")){
				parametros.setTexto9(d.getTexto());
			}
			else if(d.getNombrecampo().equals("text10")){
				parametros.setTexto10(d.getTexto());
			}
			else if(d.getNombrecampo().equals("text11")){
				parametros.setTexto11(d.getTexto());
			}
			else if(d.getNombrecampo().equals("text12")){
				parametros.setTexto12(d.getTexto());
			}
			else if(d.getNombrecampo().equals("cordialmente")){
				parametros.setCordialmente(d.getTexto());
			}
			else if(d.getNombrecampo().equals("firma")){
				parametros.setFirma(d.getTexto());
			}
			else if(d.getNombrecampo().equals("nombrefirma")){
				parametros.setNombrefirma(d.getTexto());
			}
			else if(d.getNombrecampo().equals("cargo")){
				parametros.setCargofirma(d.getTexto());
			}
			else if(d.getNombrecampo().equals("piedepagina")){
				parametros.setPiedepagina(d.getTexto());
			}
			else if(d.getNombrecampo().equals("codigoverificacion")){
				parametros.setCodigoverificaciontexto(d.getTexto());
			}
			else if(d.getNombrecampo().equals("version")){
				parametros.setVersion(d.getTexto());
			}
			
			
		}
		
		
		
		return parametros;
		
		
		
	}
		
	
	

}
