package com.drewhamlett.nodejs;

import java.awt.Image;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.*;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

public class NodeJSProjectLogicalView implements LogicalViewProvider {

	private final NodeJSProject project;

	public NodeJSProjectLogicalView( NodeJSProject project ) {
		this.project = project;
	}

	@Override
	public org.openide.nodes.Node createLogicalView() {
		
		return new NodeJSProjectRootNode(project);

		/*FileObject root = project.getProjectDirectory();
		DataFolder findFolder = DataFolder.findFolder( root );
		Node node = findFolder.getNodeDelegate();

		return new TextNode( node, project );*/

		//for ( int i = 0; i < nodes.length; i++ ) {
		//nodes[i].getChildren();
		//}

		//return new TextNode(node, node.getChildren(), project );
		/*try {

		 FileObject root = project.getProjectDirectory();
		 DataFolder findFolder = DataFolder.findFolder( root );
		 Node node = findFolder.getNodeDelegate();

		 return new TextNode( node, project );

		 } catch ( DataObjectNotFoundException donfe ) {
		 Exceptions.printStackTrace( donfe );
		 return new AbstractNode( Children.LEAF );
		 }*/
	}

	 @Override
    public Node findPath(Node root, Object target) {
        Project p = root.getLookup().lookup(Project.class);
        if (p == null) {
            return null;
        }
        // Check each child node in turn.
        Node[] children = root.getChildren().getNodes(true);
        for (Node node : children) {
            if (target instanceof DataObject || target instanceof FileObject) {
                FileObject kidFO = node.getLookup().lookup(FileObject.class);
                if (kidFO == null) {
                    continue;
                }
                // Copied from org.netbeans.spi.java.project.support.ui.TreeRootNode.PathFinder.findPath:
                FileObject targetFO = null;
                if (target instanceof DataObject) {
                    targetFO = ((DataObject) target).getPrimaryFile();
                } else {
                    targetFO = (FileObject) target;
                }
                Project owner = FileOwnerQuery.getOwner(targetFO);
                if (!p.equals(owner)) {
                    return null; // Don't waste time if project does not own the fileobject
                }
                if (kidFO == targetFO) {
                    return node;
                } else if (FileUtil.isParentOf(kidFO, targetFO)) {
                    String relPath = FileUtil.getRelativePath(kidFO, targetFO);

                    // first path without extension (more common case)
                    String[] path = relPath.split("/"); // NOI18N
                    path[path.length - 1] = targetFO.getName();

                    // first try to find the file without extension (more common case)
                    Node found = findNode(node, path);
                    if (found == null) {
                        // file not found, try to search for the name with the extension
                        path[path.length - 1] = targetFO.getNameExt();
                        found = findNode(node, path);
                    }
                    if (found == null) {
                        // can happen for tests that are underneath sources directory
                        continue;
                    }
                    if (hasObject(found, target)) {
                        return found;
                    }
                    Node parent = found.getParentNode();
                    Children kids = parent.getChildren();
                    children = kids.getNodes();
                    for (Node child : children) {
                        if (hasObject(child, target)) {
                            return child;
                        }
                    }
                }
            }
        }
        return null;
    }
	 
	 private Node findNode(Node start, String[] path) {
        Node found = null;
        try {
            found = NodeOp.findPath(start, path);
        } catch (NodeNotFoundException ex) {
            // ignored
        }
        return found;
    }

    private boolean hasObject(Node node, Object obj) {
        if (obj == null) {
            return false;
        }
        FileObject fileObject = node.getLookup().lookup(FileObject.class);
        if (fileObject == null) {
            return false;
        }
        if (obj instanceof DataObject) {
            DataObject dataObject = node.getLookup().lookup(DataObject.class);
            if (dataObject == null) {
                return false;
            }
            if (dataObject.equals(obj)) {
                return true;
            }
            return hasObject(node, ((DataObject) obj).getPrimaryFile());
        } else if (obj instanceof FileObject) {
            return obj.equals(fileObject);
        }
        return false;
    }

	public static class NodeJSProjectRootNode extends AbstractNode {

		private NodeJSProject project;

		public NodeJSProjectRootNode( NodeJSProject project ) {
			super( createChildren( project ), Lookups.singleton( project ) );
			this.project = project;
			setIconBaseWithExtension( "/com/drewhamlett/nodejs/nodejs.png" ); // NOI18N
			setName( ProjectUtils.getInformation( project ).getDisplayName() );
		}

		@Override
		public Action[] getActions( boolean arg0 ) {

			Action[] nodeActions = new Action[ 7 ];
			nodeActions[0] = CommonProjectActions.newFileAction();
			nodeActions[1] = CommonProjectActions.copyProjectAction();
			nodeActions[2] = CommonProjectActions.deleteProjectAction();
			nodeActions[5] = CommonProjectActions.setAsMainProjectAction();
			nodeActions[6] = CommonProjectActions.closeProjectAction();


			return nodeActions;
		}

		private static Children createChildren( NodeJSProject project ) {
		
			return NodeFactorySupport.createCompositeChildren( project, "Projects/com-drewhamlett-nodejs/Nodes" ); // NOI18N
		}
	}
	/*public static class NodeJSProjectNode extends FilterNode.Children {

	 public NodeJSProjectNode( Node node ) {
	 super( node );
	 }

	 @Override
	 protected Node[] createNodes( Node key ) {

	 if ( key.getName().startsWith( "." ) ) {
	 return new Node[]{};
	 }
	 return new Node[]{ copyNode( key ) };
	 }
	 }*/
	/**
	 * This is the node you actually see in the project tab for the project
	 */
	/*
	 private static final class TextNode extends FilterNode {

	 final NodeJSProject project;

	 public TextNode( Node node, NodeJSProject project ) {
	 //super( node, Children.LEAF );
	 super( node, new NodeJSProjectNode( node ),
	 new ProxyLookup( new Lookup[]{
	 Lookups.singleton( project ),
	 node.getLookup()
	 } ) );

	 this.project = project;
	 }
	 /*public TextNode( Node node, NodeJSProject project ) {

	 super( new NodeJSProjectNode( node ) );
	 this.project = project;
	 }*/

	/*@Override
	 public Action[] getActions( boolean arg0 ) {

	 Action[] nodeActions = new Action[ 7 ];
	 nodeActions[0] = CommonProjectActions.newFileAction();
	 nodeActions[1] = CommonProjectActions.copyProjectAction();
	 nodeActions[2] = CommonProjectActions.deleteProjectAction();
	 nodeActions[5] = CommonProjectActions.setAsMainProjectAction();
	 nodeActions[6] = CommonProjectActions.closeProjectAction();


	 return nodeActions;
	 }

	 @Override
	 public Image getIcon( int type ) {
	 return Utilities.loadImage( "/com/drewhamlett/nodejs/nodejs.png" );
	 }

	 @Override
	 public Image getOpenedIcon( int type ) {
	 return getIcon( type );
	 }

	 @Override
	 public String getDisplayName() {
	 return project.getProjectDirectory().getName();
	 }
	 }*/
}
