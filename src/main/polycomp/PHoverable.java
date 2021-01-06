package main.polycomp;

/**
 * An interface for use in tracking which components are mouse-hovered.
 * 
 * Example use:
 * 
 * private Vector<PHoverable> mHoverables = new Vector<PHoverable>();
 * 
 * public void addHoverable(PHoverable item)
 * {
 *     try{
 *         if(item == null){
 *             throw new Exception("PHoverable item cannot be null.");
 *         }
 *         this.mHoverables.add(item);
 *     }catch(Exception ex){
 *         ex.printStackTrace();
 *     }
 * }
 * 
 * private void setHovered(PHoverable target)
 * {
 *     for(PHoverable ph : this.mHoverables)
 *     {
 *         if(target != null && ph == target){
 *             ph.setComponentHovered(true);
 *         }else{
 *             ph.setComponentHovered(false);
 *         }
 *     }
 *     return;
 * }
 * 
 * Your mouseMoved method might look something like this:
 * public void mouseMoved(MouseEvent e)
 * {
 *     // If your hoverable component's bounds contain(e.getX(), e.get(Y)), then...
 *     this.setHovered([hoverable component name here]);
 * }
 * 
 * @author John McCullock
 * @version 1.0 2014-10-07
 */
public interface PHoverable
{
	abstract void setComponentHovered(boolean state);
	abstract boolean componentIsHovered();
}
