public class BookLabelService {
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    
    // Generate labels based on book copy information
    public String generateLabel(Long bookCopyId) {
        BookCopy copy = bookCopyRepository.findById(bookCopyId)
            .orElseThrow(() -> new IllegalArgumentException("BookCopy not found"));
        Book book = copy.getBook();
        
        return String.format("%s\n%s\n%s\nID: %d\nLoc: %s",
            book.getTitle(),
            book.getAuthors().stream().map(Author::getName).collect(Collectors.joining(", ")),
            book.getISBN(),
            copy.getId(),
            copy.getLocation());
    }
}
