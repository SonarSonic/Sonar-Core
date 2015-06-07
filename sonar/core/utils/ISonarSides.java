package sonar.core.utils;

/**for use on tile entities with changeable import/export sides*/
public abstract interface ISonarSides
{
  /**@return if the block can be configured by a calculator wrench*/
  public abstract boolean canBeConfigured();

  /**@param side the side you wish to change*/
  public abstract void increaseSide(int side, int dimension);

  /**sets the side configurations*/
  public abstract void setSide(int side, int value);
}
