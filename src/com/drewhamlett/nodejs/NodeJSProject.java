package com.drewhamlett.nodejs;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.DeleteOperationImplementation;
import org.netbeans.spi.project.CopyOperationImplementation;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.DefaultProjectOperations;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

class NodeJSProject implements Project {

	private final FileObject projectDir;
	LogicalViewProvider logicalView = new NodeJSProjectLogicalView( this );
	private final ProjectState state;
	private Lookup lkp;

	public NodeJSProject( FileObject projectDir, ProjectState state ) {
		this.projectDir = projectDir;
		this.state = state;
	}

	@Override
	public FileObject getProjectDirectory() {
		return projectDir;
	}

	@Override
	public Lookup getLookup() {
		if ( lkp == null ) {
			lkp = Lookups.fixed( new Object[]{
						this, //project spec requires a project be in its own lookup
						state, //allow outside code to mark the project as needing saving
						new ActionProviderImpl(), //Provides standard actions like Build and Clean
						new DemoDeleteOperation(),
						new DemoCopyOperation( this ),
						new NodeJSTemplates(),
						new Info(), //Project information implementation
						logicalView, //Logical view of project implementation
					} );
		}
		return lkp;
	}

	private final class ActionProviderImpl implements ActionProvider {

		private String[] supported = new String[]{
			ActionProvider.COMMAND_DELETE,
			ActionProvider.COMMAND_COPY
		};

		@Override
		public String[] getSupportedActions() {
			return supported;
		}

		@Override
		public void invokeAction( String string, Lookup lookup ) throws IllegalArgumentException {
			if ( string.equalsIgnoreCase( ActionProvider.COMMAND_DELETE ) ) {
				DefaultProjectOperations.performDefaultDeleteOperation( NodeJSProject.this );
			}
			if ( string.equalsIgnoreCase( ActionProvider.COMMAND_COPY ) ) {
				DefaultProjectOperations.performDefaultCopyOperation( NodeJSProject.this );
			}
		}

		@Override
		public boolean isActionEnabled( String command, Lookup lookup ) throws IllegalArgumentException {
			if ( ( command.equals( ActionProvider.COMMAND_DELETE ) ) ) {
				return true;
			} else if ( ( command.equals( ActionProvider.COMMAND_COPY ) ) ) {
				return true;
			} else {
				throw new IllegalArgumentException( command );
			}
		}
	}

	private final class DemoDeleteOperation implements DeleteOperationImplementation {

		@Override
		public void notifyDeleting() throws IOException {
		}

		@Override
		public void notifyDeleted() throws IOException {
		}

		@Override
		public List<FileObject> getMetadataFiles() {
			List<FileObject> dataFiles = new ArrayList<FileObject>();
			return dataFiles;
		}

		@Override
		public List<FileObject> getDataFiles() {
			List<FileObject> dataFiles = new ArrayList<FileObject>();
			return dataFiles;
		}
	}

	private final class DemoCopyOperation implements CopyOperationImplementation {

		private final NodeJSProject project;
		private final FileObject projectDir;

		public DemoCopyOperation( NodeJSProject project ) {
			this.project = project;
			this.projectDir = project.getProjectDirectory();
		}

		@Override
		public List<FileObject> getMetadataFiles() {
			return Collections.EMPTY_LIST;
		}

		@Override
		public List<FileObject> getDataFiles() {
			return Collections.EMPTY_LIST;
		}

		@Override
		public void notifyCopying() throws IOException {
		}

		@Override
		public void notifyCopied( Project arg0, File arg1, String arg2 ) throws IOException {
		}
	}

	/**
	 * Implementation of project system's ProjectInformation class
	 */
	private final class Info implements ProjectInformation {

		@Override
		public Icon getIcon() {
			return new ImageIcon( Utilities.loadImage( "/com/drewhamlett/nodejs/nodejs.png" ) );
		}

		@Override
		public String getName() {
			return getProjectDirectory().getName();
		}

		@Override
		public String getDisplayName() {
			return getName();
		}

		@Override
		public void addPropertyChangeListener( PropertyChangeListener pcl ) {
			//do nothing, won't change
		}

		@Override
		public void removePropertyChangeListener( PropertyChangeListener pcl ) {
			//do nothing, won't change
		}

		@Override
		public Project getProject() {
			return NodeJSProject.this;
		}
	}
}
