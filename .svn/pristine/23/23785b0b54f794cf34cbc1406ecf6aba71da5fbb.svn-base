package co.sistemcobro.intranet.listener;

import java.io.File;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

import co.sistemcobro.all.util.Archivo;
import co.sistemcobro.intranet.constante.Constante;



/**
 * 
 * @author Jony Hurtado
 * 
 */
@WebListener
public class RutaTemporalListener implements ServletContextListener, HttpSessionListener {
	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getLogger(RutaTemporalListener.class);

	ResourceBundle config = ResourceBundle.getBundle(Constante.FILE_CONFIG_INTRANET);
	private String temp_file_global = config.getString("app.file.temporal.global");

	public void sessionCreated(HttpSessionEvent he) {
		String contextpath = he.getSession().getServletContext().getContextPath();
		crearRutaTemporal(getNombreAplicacion(contextpath), false);
	}

	public void sessionDestroyed(HttpSessionEvent he) {
	}

	/* Application Startup Event */
	public void contextInitialized(ServletContextEvent ce) {
		String contextpath = ce.getServletContext().getContextPath();
		crearRutaTemporal(getNombreAplicacion(contextpath), true);
	}

	/* Application Shutdown Event */
	public void contextDestroyed(ServletContextEvent ce) {
		String contextpath = ce.getServletContext().getContextPath();
		eliminarRutaTemporal(getNombreAplicacion(contextpath));
	}

	private void crearRutaTemporal(String aplicacion, boolean reconstruir) {
		Constante.ROOT_FILE_TEMPORARY = temp_file_global + File.separator + aplicacion;

		File file_global = new File(temp_file_global);

		if (!file_global.exists()) {
			file_global.mkdir();
		}
		File file_local = new File(Constante.ROOT_FILE_TEMPORARY);
		if (!file_local.exists()) {
			file_local.mkdir();
		} else {
			if (reconstruir) {
				Archivo.eliminarDirectorio(Constante.ROOT_FILE_TEMPORARY);
				file_local.mkdir();
				logger.info("ROOT_FILE_TEMPORARY:" + file_local.getAbsolutePath());
			}
		}
	}

	private void eliminarRutaTemporal(String aplicacion) {
		Constante.ROOT_FILE_TEMPORARY = temp_file_global + File.separator + aplicacion;
		File file_local = new File(Constante.ROOT_FILE_TEMPORARY);
		if (file_local.exists()) {
			Archivo.eliminarDirectorio(Constante.ROOT_FILE_TEMPORARY);
		}
	}

	private String getNombreAplicacion(String contextPath) {
		Pattern patron = Pattern.compile("[^A-Za-z0-9_-]+");
		Matcher encaja = patron.matcher(contextPath);
		return encaja.replaceAll("");
	}

}
