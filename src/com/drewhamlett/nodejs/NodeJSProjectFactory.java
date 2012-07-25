package com.drewhamlett.nodejs;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider( service = ProjectFactory.class )
public class NodeJSProjectFactory implements ProjectFactory {

	public static final String PROJECT_DEFINE = "package.json";

	@Override
	public boolean isProject( FileObject fo ) {
		return fo.getFileObject( PROJECT_DEFINE ) != null;
	}

	//Specifies when the project will be opened, i.e.,
	//if the project exists:
	@Override
	public Project loadProject( FileObject dir, ProjectState state ) throws IOException {
		return isProject( dir ) ? new NodeJSProject( dir, state ) : null;
	}


	@Override
	public void saveProject( final Project project ) throws IOException, ClassCastException {
		FileObject projectRoot = project.getProjectDirectory();
		if ( projectRoot.getFileObject( PROJECT_DEFINE ) == null ) {
			throw new IOException( "Project dir " + projectRoot.getPath()
					+ " deleted,"
					+ " cannot save project" );
		}	
	}
}
