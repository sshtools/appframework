package plugspud;

import java.util.StringTokenizer;

/**
 *
 */
public class SortCriteria {
    public final static int NO_SORT = 0;
    public final static int SORT_ASCENDING = 1;
    public final static int SORT_DESCENDING = 2;
    private boolean foldersFirst = true, caseSensitive = false;
    private int sortDirection;
    private int sortType;

    public SortCriteria() {
        this(0, SORT_ASCENDING, true, false);
    }

    public SortCriteria(int sortType, int sortDirection, boolean foldersFirst,
                        boolean caseSensitive) {
        setSortType(sortType);
        setSortDirection(sortDirection);
        setFoldersFirst(foldersFirst);
        setCaseSensitive(caseSensitive);
    }

    public SortCriteria(String string) {
      this(0, 1, true, false);
      try {
        StringTokenizer t = new StringTokenizer(string, ",");
        sortType = Integer.parseInt(t.nextToken());
        sortDirection = Integer.parseInt(t.nextToken());
        foldersFirst = t.nextToken().equals("true");
        caseSensitive = t.nextToken().equals("true");
      }
      catch(Throwable t) {
        
      }
    }

    public int getSortDirection() {
        return sortDirection;
    }


    public int getSortType() {
        return sortType;
    }


    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public boolean isFoldersFirst() {
        return foldersFirst;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }


    public void setFoldersFirst(boolean foldersFirst) {
        this.foldersFirst = foldersFirst;
    }

    public void setSortDirection(int sortDirection) {
        this.sortDirection = sortDirection;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }
    
    @Override
	public String toString() {
      return sortType + "," + sortDirection + "," + foldersFirst + "," + caseSensitive;
    }
}
