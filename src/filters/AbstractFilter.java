/*
 * TCSS 305 - SnapShop
 */

package filters;

import image.Pixel;
import image.PixelImage;

/**
 * Abstract superclass for all image filters.
 * 
 * @author Marty Stepp
 * @author Daniel M. Zimmerman
 * @author Alan Fowler
 * @version 1.1
 */
/*TODO Refactoring #1: Inline variable and simplify arithmetic.
 * Scope: Entire class.
 * Author: Zitao Yu
 */

/*TODO Refactoring #2: Change method declaration.
 * Scope: Entire class.
 * Author: Zitao Yu
 */

/*TODO Refactoring #5: Rename variable.
 * Scope: Entire class.
 * Author: Zitao Yu
 */
public abstract class AbstractFilter implements Filter {
    /**
     * The "Filter" suffix.
     */
    private static final String FILTER_SUFFIX = "Filter";

    /**
     * The description of this filter (will be used on buttons).
     */
    private String myDescription;

    // Constructor

    /**
     * Constructs a filter and uses a modified version of the class's name as
     * its description. Any package hierarchy in the name is removed, and the
     * word "Filter" is removed from the end (if present). For example, the
     * class "snapshot.filters.EdgeDetectFilter" would end up with "EdgeDetect"
     * as its description.
     */
    /*TODO Refactoring #8: Remove unused constructor.
     * Scope: Entire class.
     * Author: Zitao Yu
     */
    
//    protected AbstractFilter() {
//        final String name = getClass().getName();
//        final int dot = name.lastIndexOf('.');
//        
//        //TODO Hyeong: 1. Changing multiple lines of if/else into in-line function which is shorter
//        myDescription = (dot >= 0 && name.endsWith(FILTER_SUFFIX)) ? 
//                                        (myDescription = name.substring(dot + 1, name.length() - FILTER_SUFFIX.length())) 
//                                        : (myDescription = name.substring(dot + 1, name.length()));
//        
//        
////        if (dot >= 0 && name.endsWith(FILTER_SUFFIX)) {
////            // truncate the word "Filter"
////            myDescription = name.substring(dot + 1, name.length() - FILTER_SUFFIX.length());
////        } else {
////            myDescription = name.substring(dot + 1, name.length());
////        }
//    }

    /**
     * Constructs a filter with the specified description.
     * 
     * @param theDescription The description.
     */
    public AbstractFilter(final String theDescription) {
        myDescription = theDescription;
    }

    // Instance Methods

    /**
     * Returns the text description of this filter.
     * 
     * @return the text description of this filter
     */
    @Override
    public String getDescription() {
        return myDescription;
    }

    /**
     * Applies a "weighting" to each pixel, where its new value is produced by
     * doing a weighted average of the 3x3 grid of pixels around it. For
     * example, A Gaussian blur/softening effect can be achieved by applying the
     * following weights to each pixel:
     * 
     * <pre>
     *    1  2  1
     *    2  4  2
     *    1  2  1
     * </pre>
     * 
     * Since the weights increase the pixel's color value, likely beyond the
     * legal maximum color value of 255, a scale-down is applied based on the
     * sum of the weights.
     * 
     * @param theImage The image.
     * @param theWeights The weights matrix. This must be a non-null 3x3 matrix
     *            or an IllegalArgumentException is thrown.
     * @exception IllegalArgumentException if the weights are invalid.
     */
    protected void applyPixelWeight(final PixelImage theImage, final int[][] theWeights)
        throws IllegalArgumentException {
        
        checkWeights(theWeights);

        int sum = 0;

        //TODO 1. Akshdeep: Simplify Arithmetic. Sum = sum + col to sum += col.
        for (final int[] row : theWeights) {
            for (final int col : row) {
                //sum = sum + col
                sum += col;
            }
        }
        if (sum == 0) {
            // sum = sum + 1;
            sum++;
        }

        applyPixelWeight(theImage, theWeights, sum);
    }

    /**
     * Applies a "weighting" to each pixel, with the given scale-down factor.
     * 
     * @param theImage The image.
     * @param theWeights The weights matrix. This must be a non-null 3x3 matrix
     *            or an IllegalArgumentException is thrown.
     * @param theScale The scale-down factor.
     * @exception IllegalArgumentException if the weights are invalid.
     * @see #weight(PixelImage, int[][])
     */
    
    protected void applyPixelWeight(final PixelImage theImage, final int[][] theWeights,
                          final int theScale) throws IllegalArgumentException {
        checkWeights(theWeights);
//        final int w = theImage.getWidth(null);
//        final int h = theImage.getHeight(null);
        final int width = theImage.getWidth(null);
        final int height = theImage.getHeight(null);
        final Pixel[][] oldPixels = theImage.getPixelData();
        final Pixel[][] newPixels = new Pixel[height][width];

        for (int yAxis = 0; yAxis < height; yAxis++) {
            for (int xAxis = 0; xAxis < width; xAxis++) {
                // add up 9 neighboring pixels
            	//TODO 6. Akshdeep: declare variable in single line
                int red = 0, blue = 0, green = 0;
                for (int j = Math.max(0, yAxis - 1); j <= Math.min(yAxis + 1, height - 1); j++) {
                    for (int i = Math.max(0, xAxis - 1); i <= Math.min(xAxis + 1, width - 1); i++) {
                        // Pixel p = oldPixels[i][j];
//                        final Pixel p = oldPixels[j][i];
//                        final int weight = theWeights[y - j + 1][x - i + 1];
//                        red = red + p.getRed() * weight;
//                        green = green + p.getGreen() * weight;
//                        blue = blue + p.getBlue() * weight;
                        
                    	//TODO 7. Akshdeep: declare local variable pixel of type Pixel in order to make code more readable.
                        final Pixel pixel = oldPixels[j][i];
                        //TODO 8. Akshdeep: declare local variable 'k' of type int in order to make code more readable.
						final int k = theWeights[yAxis - j + 1][xAxis - i + 1];
						red += pixel.getRed() * k;
                        green += pixel.getGreen() * k;
                        blue += pixel.getBlue() * k;
                    }
                }

                // account for negative / too high color values
                red = normalizeColor(red / theScale);
                green = normalizeColor(green / theScale);
                blue = normalizeColor(blue / theScale);

                newPixels[yAxis][xAxis] = new Pixel(red, green, blue);
            }
        }

        theImage.setPixelData(newPixels);
    }

    /**
     * Checks to see if the specified weights matrix is valid (that is, is
     * non-null and a Pixel.NUM_CHANNELS-square grid).
     * 
     * @param theWeights The weights matrix.
     * @exception IllegalArgumentException if the weights matrix is invalid.
     */
    private void checkWeights(final int[][] theWeights) throws IllegalArgumentException {
        
        //TODO Hyeong: 2. Decompose Conditional
        if (validMatrix(theWeights)) {
            throw new IllegalArgumentException("must pass correctly-sized grid");
        }
    }
    
    private boolean validMatrix(final int[][] theWeights)
    {
        return (theWeights == null) || (theWeights.length != Pixel.NUM_CHANNELS)
                                        || (theWeights[0] == null) || (theWeights[0].length != Pixel.NUM_CHANNELS)
                                        || (theWeights[1] == null) || (theWeights[1].length != Pixel.NUM_CHANNELS)
                                        || (theWeights[2] == null) || (theWeights[2].length != Pixel.NUM_CHANNELS);
    }
    

    /**
     * Normalizes the specified color value to the range 0-255.
     * 
     * @param theColor The color value.
     * @return the normalized color value.
     */
    protected int normalizeColor(final int theColor) {
        return Math.max(Pixel.MIN_COLOR_VALUE, Math.min(Pixel.MAX_COLOR_VALUE, theColor));
    }

    /**
     * Swaps the specified pixels in the image.
     * 
     * @param theData The image data.
     * @param row1 The row of the first pixel to swap.
     * @param col1 The column of the first pixel to swap.
     * @param row2 The row of the second pixel to swap.
     * @param col2 The column of the second pixel to swap.
     */
    protected void swapPixel(final Pixel[][] theData, final int row1, final int col1,
                        final int row2, final int col2) {
        //final Pixel temp = theData[row1][col1];
        final Pixel tempPixel = theData[row1][col1];
        theData[row1][col1] = theData[row2][col2];
        //theData[row2][col2] = temp;
        theData[row2][col2] = tempPixel;
    }
}
