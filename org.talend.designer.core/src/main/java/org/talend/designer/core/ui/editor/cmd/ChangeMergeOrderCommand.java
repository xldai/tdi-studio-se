// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.core.ui.editor.cmd;

import java.util.List;

import org.eclipse.gef.commands.Command;
import org.talend.core.model.components.IComponent;
import org.talend.designer.core.i18n.Messages;
import org.talend.designer.core.ui.editor.connections.Connection;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.editor.process.Process;

/**
 * DOC nrousseau class global comment. Detailled comment <br/>
 * 
 */
public class ChangeMergeOrderCommand extends Command {

    private Node mergeNode;

    private List<Connection> connectionInNewOrder;

    private List<Connection> connectionInOldOrder;

    private String nodeType;

    public ChangeMergeOrderCommand(Node mergeNode, List<Connection> inputConnections) {
        this.mergeNode = mergeNode;
        this.connectionInNewOrder = inputConnections;

        this.nodeType = this.mergeNode.getComponent().getComponentType();
        if (nodeType.equals(IComponent.MULTIPLE_IN_SINGLE_OUT_TYPE)) {
            this.connectionInOldOrder = (List<Connection>) mergeNode.getIncomingConnections();
        } else if (nodeType.equals(IComponent.SINGLE_IN_MULTIPLE_OUT_TYPE)) {
            this.connectionInOldOrder = (List<Connection>) mergeNode.getOutgoingConnections();
        } else {
            this.connectionInOldOrder = (List<Connection>) mergeNode.getIncomingConnections();
        }

        if (connectionInNewOrder.size() != connectionInOldOrder.size()) {
            throw new IllegalArgumentException("new connection list should have the same size as the old one"); //$NON-NLS-1$
        }
        this.setLabel(Messages.getString("ChangeMergeOrderCommand.label")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.commands.Command#execute()
     */
    @Override
    public void execute() {

        if (nodeType.equals(IComponent.MULTIPLE_IN_SINGLE_OUT_TYPE)) {
            mergeNode.setIncomingConnections(connectionInNewOrder);
        } else if (nodeType.equals(IComponent.SINGLE_IN_MULTIPLE_OUT_TYPE)) {
            mergeNode.setOutgoingConnections(connectionInNewOrder);
        } else {
            mergeNode.setIncomingConnections(connectionInNewOrder);
        }

        connectionInNewOrder.get(0).updateAllId();
        ((Process) mergeNode.getProcess()).checkStartNodes();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.commands.Command#undo()
     */
    @Override
    public void undo() {
        mergeNode.setIncomingConnections(connectionInOldOrder);
        connectionInOldOrder.get(0).updateAllId();
        ((Process) mergeNode.getProcess()).checkStartNodes();
    }

}
