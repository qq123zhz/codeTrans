package codetrans.popup.actions;

import java.io.File;
import java.io.FilenameFilter;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import codetrans.test.FileCharsetConverter;

public class Utf82Gbk implements IObjectActionDelegate {

	private Shell shell;
	private ISelection selection;

	/**
	 * Constructor for Action1.
	 */
	public Utf82Gbk() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		if(selection instanceof IStructuredSelection){
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object object = structuredSelection.getFirstElement();
			IJavaProject javaProject = null;
			File parentFile = null;
			if(object instanceof IProject){
				IProject iProject =  (IProject) object;
				parentFile = iProject.getLocation().toFile();
				javaProject = JavaCore.create(iProject); 
			}else if(object instanceof IJavaProject){
				javaProject = (IJavaProject) object;
				parentFile = javaProject.getProject().getLocation().toFile();
			}
			
			try {
				javaProject.getProject().refreshLocal(0, null);
				IPackageFragmentRoot[] fragmentRoots = 	javaProject.getPackageFragmentRoots();
				if(fragmentRoots==null)return;
				for (IPackageFragmentRoot iPackageFragmentRoot : fragmentRoots) {
					if((iPackageFragmentRoot instanceof JarPackageFragmentRoot))continue;
					File file =   iPackageFragmentRoot.getPath().toFile();
					File f  = new File(parentFile, file.getName());
					FileCharsetConverter.convert(f,"UTF-8","GBK",  new FilenameFilter() {  
				        @Override  
				        public boolean accept(File dir, String name) {  
				        	if(dir.isDirectory())return true;
				            return name.endsWith("java");  
				        }  
				    });
				}
				javaProject.getProject().setDefaultCharset("GBK",null);
				javaProject.getProject().refreshLocal(0, null);
			} catch (JavaModelException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
