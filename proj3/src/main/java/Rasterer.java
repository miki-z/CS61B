//import spark.Route;
// import sun.lwawt.macosx.CSystemTray;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {

    private static final double ROOT_LONDPP = (MapServer.ROOT_LRLON - MapServer.ROOT_ULLON)
            / MapServer.TILE_SIZE;
    private static final int MAX_DEPTH = 7;

    public Rasterer() {
        // YOUR CODE HERE
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        // System.out.println(params);
        Map<String, Object> results = new HashMap<>();
        boolean querySuccessFlag = true;
        // Calculate the longitudinal distance per pixel
        double userLrLon = params.get("lrlon");
        double userUlLon = params.get("ullon");
        double userLrLat = params.get("lrlat");
        double userUlLat = params.get("ullat");
        double width = params.get("w");
        double height = params.get("h");
        double lonDPP = (userLrLon - userUlLon) / width;
        // Check for valid parameters
        if ((userLrLat < MapServer.ROOT_LRLAT && userLrLon < MapServer.ROOT_LRLON
                && userUlLat > MapServer.ROOT_ULLAT && userUlLon > MapServer.ROOT_ULLON)
                || userUlLon > userLrLon || userLrLat > userUlLat) {
            querySuccessFlag = false;
        }
        // Determine the depth for the query
        int depth = 0;
        while (lonDPP < ROOT_LONDPP && depth < MAX_DEPTH) {
            depth++;
            lonDPP *= 2;
        }
        // Determine the bounding upper left longitude of the rastered image
        int tilesPerLine = (int) Math.pow(2, depth);
        double lineTileSize = (MapServer.ROOT_LRLON - MapServer.ROOT_ULLON) / tilesPerLine;
        double colTileSize = (MapServer.ROOT_LRLAT - MapServer.ROOT_ULLAT) / tilesPerLine;
        int ulLonIndex = Math.max(0,
                (int) ((userUlLon - MapServer.ROOT_ULLON) / lineTileSize));
        int lrLonIndex = Math.min(tilesPerLine - 1,
                (int) ((userLrLon - MapServer.ROOT_ULLON) / lineTileSize));
        int ulLatIndex = Math.max(0,
                (int) ((userUlLat - MapServer.ROOT_ULLAT) / colTileSize));
        int lrLatIndex = Math.min(tilesPerLine - 1,
                (int) ((userLrLat - MapServer.ROOT_ULLAT) / colTileSize));

        String[][] rasterGrid = gridRender(ulLonIndex, lrLonIndex, ulLatIndex, lrLatIndex, depth);
        // System.out.println("Since you haven't implemented getMapRaster, nothing is displayed in "
        //                   + "your browser.");

        double rasterUlLon = MapServer.ROOT_ULLON + ulLonIndex * lineTileSize;
        double rasterUlLat = MapServer.ROOT_ULLAT + ulLatIndex * colTileSize;
        double rasterLrLon = MapServer.ROOT_ULLON + (lrLonIndex + 1) * lineTileSize;
        double rasterLrLat = MapServer.ROOT_ULLAT + (lrLatIndex + 1) * colTileSize;

        // Push all raster parameters into results
        results.put("render_grid", rasterGrid);
        results.put("raster_ul_lon", rasterUlLon);
        results.put("raster_ul_lat", rasterUlLat);
        results.put("raster_lr_lon", rasterLrLon);
        results.put("raster_lr_lat", rasterLrLat);
        results.put("depth", depth);
        results.put("query_success", querySuccessFlag);
        return results;
    }

    private String[][] gridRender(int ulLonIndex, int lrLonIndex, int ulLatIndex,
                                  int lrLatIndex, int depth) {
        String[][] grid = new String[lrLatIndex - ulLatIndex + 1][lrLonIndex - ulLonIndex + 1];
        int row = 0;
        for (int y = ulLatIndex; y <= lrLatIndex; y++) {
            int col = 0;
            for (int x = ulLonIndex; x <= lrLonIndex; x++) {
                grid[row][col] = "d" + depth + "_x" + x + "_y"  + y + ".png";
                col++;
            }
            row++;
        }
        return grid;
    }



}
