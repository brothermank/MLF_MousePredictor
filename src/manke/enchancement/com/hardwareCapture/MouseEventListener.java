package manke.enchancement.com.hardwareCapture;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import manke.enchancement.com.gui.NetworkIllustrator;

public class MouseEventListener implements MouseListener {

	NetworkIllustrator ni;
	
	public MouseEventListener(NetworkIllustrator networkIllustrator) {
		ni = networkIllustrator;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		ni.getNeuronAtPos(e.getX(), e.getY());
		// TODO Auto-generated method stub
		
	}

}
