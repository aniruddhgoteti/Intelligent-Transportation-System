import java.awt.*;
import java.util.LinkedList;
import java.awt.image.BufferedImage;/* Component to show images */
class ImageComponent extends javax.swing.JComponent {
private LinkedList imageList = new LinkedList();
private LinkedList rectList = new LinkedList();
	private static final int	PADDING = 5;
	public ImageComponent() {}	
public void addImage(BufferedImage image) {	
	if (image == null)		return;
		// add image to list		
imageList.add(image);
		rectList.add(new Rectangle(0, 0, image.getWidth(), image.getHeight()));
		// update scroll bar
	resizeScroller();	}
	public void clearImages() {		// clear lists
		imageList.clear();
		rectList.clear();		// update scroll bar
		resizeScroller();	}	// assuming we're inside a scrollbar view	// resizeScroller updates our preferred size	
// and tells the view to revalidate its bars	
private void resizeScroller() {	Rectangle	bounds = new Rectangle(0, 0, 0, 0);
		// calculate bounds of images
	for (int i = 0; i < imageList.size(); ++i)
 {Rectangle rect = (Rectangle)rectList.get(i);
		bounds.height = Math.max(bounds.height, rect.height);
			bounds.width += rect.width;
		}
		bounds.width += PADDING * (imageList.size() + 1);
		bounds.height += 2*PADDING;
		setPreferredSize(bounds.getSize());
		revalidate();	}	// paint the images
	public void paint(Graphics g) {	int offset = PADDING;
for (int i = 0; i < imageList.size(); ++i) {
g.drawImage((BufferedImage)imageList.get(i), offset, PADDING, null);
		offset += PADDING + ((Rectangle)rectList.get(i)).width;
		}	}}

