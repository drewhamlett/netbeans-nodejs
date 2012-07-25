/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drewhamlett.nodejs;

/**
 *
 * @author drewh
 */
import java.awt.Image;
import java.util.List;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.filesystems.FileObject;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;
import org.openide.awt.Actions;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

public class NodeJSChildFactory extends ChildFactory<Action> {

	private FileObject folder = null;

	public NodeJSChildFactory( FileObject folder ) {
		this.folder = folder;
	}

	@Override
	protected boolean createKeys( List<Action> toPopulate ) {
		for ( Action action : Lookups.forPath( folder.getPath() ).lookupAll( Action.class ) ) {
			toPopulate.add( action );
		}
		return true;
	}

	@Override
	protected Node createNodeForKey( Action key ) {
		return new ExplorerLeafNode( key );
	}

	public static class ExplorerLeafNode extends AbstractNode {

		private Action action = null;

		public ExplorerLeafNode( Action action ) {
			super( Children.LEAF );
			this.action = action;
			setDisplayName( Actions.cutAmpersand( ( String ) action.getValue( Action.NAME ) ) );
		}

		@Override
		public Action getPreferredAction() {
			return action;
		}

		@Override
		public Image getIcon( int type ) {
			ImageIcon img = ( ImageIcon ) action.getValue( Action.SMALL_ICON );
			if ( img != null ) {
				return img.getImage();
			} else {
				return null;
			}
		}
	}
}
