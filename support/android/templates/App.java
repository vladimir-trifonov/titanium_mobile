/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by 
 * Appcelerator. It should not be modified by hand.
 */
package ${config['appid']};

% if runtime == "v8":
import org.appcelerator.kroll.runtime.v8.V8Runtime;
% else:
import org.appcelerator.kroll.runtime.rhino.RhinoRuntime;
import org.appcelerator.kroll.runtime.rhino.KrollBindings;
% endif

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollModuleInfo;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiRootActivity;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;


public final class ${config['classname']}Application extends TiApplication
{
	private static final String TAG = "${config['classname']}Application";

	@Override
	public void onCreate()
	{
		super.onCreate();

		appInfo = new ${config['classname']}AppInfo(this);
		postAppInfo();

		% if runtime == "v8":
		V8Runtime runtime = new V8Runtime();
		% else:
		RhinoRuntime runtime = new RhinoRuntime();
		% endif

		% if runtime == "v8":

		% for module in custom_modules:
		<%
		manifest = module['manifest']
		className = manifest.name[0:1].upper() + manifest.name[1:]
		%>
		runtime.addExternalModule("${manifest.moduleid}", ${manifest.moduleid}.${className}Bootstrap.class);
		% endfor

		% endif

		KrollRuntime.init(this, runtime);

		stylesheet = new ApplicationStylesheet();
		postOnCreate();

		<%def name="onAppCreate(module)" filter="trim">
			% if module['on_app_create'] != None:
			try {
				${module['class_name']}.${module['on_app_create']}(this);
			} catch (Exception e) {
				StringBuilder error = new StringBuilder();
				error.append("Error running onAppCreate method ")
					.append("\"${module['on_app_create']}\"")
					.append(" for module ")
					.append("${module.get('api_name') or module.get('manifest').moduleid}: ")
					.append(e.getMessage());
				Log.e(TAG, error.toString(), e);
			}
			% endif
		</%def>

		% for module in app_modules:
		${onAppCreate(module)} \
		% endfor

		% if len(custom_modules) > 0:
		// Custom modules
		KrollModuleInfo moduleInfo;
		% endif

		% for module in custom_modules:
		${onAppCreate(module)} \

		<% manifest = module['manifest'] %>
		% if runtime == "rhino":
		KrollBindings.addExternalBinding("${manifest.moduleid}", ${module['class_name']}Prototype.class);
		${manifest.moduleid}.${manifest.name}GeneratedBindings.init();
		% endif

		moduleInfo = new KrollModuleInfo(
			"${manifest.name}", "${manifest.moduleid}", "${manifest.guid}", "${manifest.version}",
			"${manifest.description}", "${manifest.author}", "${manifest.license}",
			"${manifest.copyright}");

		% if manifest.has_property("licensekey"):
		moduleInfo.setLicenseKey("${manifest.licensekey}");
		% endif

		KrollModule.addCustomModuleInfo(moduleInfo);
		% endfor
	}

	@Override
	public void verifyCustomModules(TiRootActivity rootActivity)
	{
		% if config['deploy_type'] != 'production':
		org.appcelerator.titanium.TiVerify verify = new org.appcelerator.titanium.TiVerify(rootActivity, this);
		verify.verify();
		% endif
	}
}
