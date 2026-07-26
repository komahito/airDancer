import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

FileModel model = new FileModel();
JTable table = new JTable(model);
DropTargetListener dtl = new DropTargetAdapter() {
  @Override public void dragOver(DropTargetDragEvent dtde) {
    if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
      dtde.acceptDrag(DnDConstants.ACTION_COPY);
      return;
    }
    dtde.rejectDrag();
  }

  @Override public void drop(DropTargetDropEvent dtde) {
    try {
      if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
        dtde.acceptDrop(DnDConstants.ACTION_COPY);
        Transferable transferable = dtde.getTransferable();
        List list = (List) transferable.getTransferData(
            DataFlavor.javaFileListFlavor);
        for (Object o: list) {
          if (o instanceof File) {
            File file = (File) o;
            model.addFileName(
                new FileName(file.getName(), file.getAbsolutePath()));
          }
        }
        dtde.dropComplete(true);
        return;
      }
    } catch (UnsupportedFlavorException | IOException ex) {
      ex.printStackTrace();
    }
    dtde.rejectDrop();
  }
};
new DropTarget(table, DnDConstants.ACTION_COPY, dtl, true);
