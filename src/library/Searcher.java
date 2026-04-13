package library;

abstract class Searcher {
    
    //Private instance variable bernama "searchInput" bertipe string
    private String searchInput;

    public Searcher(String searchinput) {
        this.searchInput = searchinput;
    }
    
    //Method untuk mendapatkan inputan search dari variable "searchInput"
    public String getSearchInput() {
        return searchInput;
    }
    
}
