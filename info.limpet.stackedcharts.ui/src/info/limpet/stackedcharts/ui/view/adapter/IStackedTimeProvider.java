package info.limpet.stackedcharts.ui.view.adapter;

public interface IStackedTimeProvider
{
  /** configure to send time updates to this listener
   * 
   * @param listener
   */
  void controlThis(IStackedTimeListener listener);
  
  /** stop sending time updates to this listener
   * 
   * @param listener
   */
  void releaseThis(IStackedTimeListener listener);

  /** identify if this object is able to provide time control
   * (we only wish to allow one)
   * @return yes/no
   */
  boolean canProvideControl();  
}
