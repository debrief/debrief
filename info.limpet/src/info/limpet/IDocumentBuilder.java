package info.limpet;

/** parent for document builder classes
 * 
 * @author Ian
 *
 */
public interface IDocumentBuilder<T extends Object>
{
  /** convert one-self into a document
   * 
   * @return
   */
  IDocument<T> toDocument();
  

  /** add an indexed value to this builder
   * 
   * @param index
   * @param value
   */
  public void add(double index, T value);

  /** add a value to this builder
   * 
   * @param value
   */
  public void add(T value);

}
