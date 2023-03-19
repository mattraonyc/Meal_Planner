class Book {

    private String title;
    private int yearOfPublishing;
    private String[] authors;

    public Book(String title, int yearOfPublishing, String[] authors) {
        this.title = title;
        this.yearOfPublishing = yearOfPublishing;
        this.authors = authors;
    }

    public String toString() {
        return "title=" + this.title + ",yearOfPublishing=" + this.yearOfPublishing + ",authors=["
            + String.join(",", this.authors) + "]";
    }
}
