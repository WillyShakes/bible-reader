package domain.model

/**
 * The 66-book Protestant canon in canonical order.
 * Source of truth for book names (FR + EN), chapter counts, and navigation order.
 * Never modified at runtime — treat as a compile-time constant.
 */
object CanonicalBooks {

    val all: List<BibleBook> = listOf(
        BibleBook("GEN", "Genèse",          "Genesis",          50, 1),
        BibleBook("EXO", "Exode",            "Exodus",           40, 2),
        BibleBook("LEV", "Lévitique",        "Leviticus",        27, 3),
        BibleBook("NUM", "Nombres",          "Numbers",          36, 4),
        BibleBook("DEU", "Deutéronome",      "Deuteronomy",      34, 5),
        BibleBook("JOS", "Josué",            "Joshua",           24, 6),
        BibleBook("JDG", "Juges",            "Judges",           21, 7),
        BibleBook("RUT", "Ruth",             "Ruth",              4, 8),
        BibleBook("1SA", "1 Samuel",         "1 Samuel",         31, 9),
        BibleBook("2SA", "2 Samuel",         "2 Samuel",         24, 10),
        BibleBook("1KI", "1 Rois",           "1 Kings",          22, 11),
        BibleBook("2KI", "2 Rois",           "2 Kings",          25, 12),
        BibleBook("1CH", "1 Chroniques",     "1 Chronicles",     29, 13),
        BibleBook("2CH", "2 Chroniques",     "2 Chronicles",     36, 14),
        BibleBook("EZR", "Esdras",           "Ezra",             10, 15),
        BibleBook("NEH", "Néhémie",          "Nehemiah",         13, 16),
        BibleBook("EST", "Esther",           "Esther",           10, 17),
        BibleBook("JOB", "Job",              "Job",              42, 18),
        BibleBook("PSA", "Psaumes",          "Psalms",          150, 19),
        BibleBook("PRO", "Proverbes",        "Proverbs",         31, 20),
        BibleBook("ECC", "Ecclésiaste",      "Ecclesiastes",     12, 21),
        BibleBook("SNG", "Cantique des Cantiques", "Song of Solomon", 8, 22),
        BibleBook("ISA", "Ésaïe",            "Isaiah",           66, 23),
        BibleBook("JER", "Jérémie",          "Jeremiah",         52, 24),
        BibleBook("LAM", "Lamentations",     "Lamentations",      5, 25),
        BibleBook("EZK", "Ézéchiel",         "Ezekiel",          48, 26),
        BibleBook("DAN", "Daniel",           "Daniel",           12, 27),
        BibleBook("HOS", "Osée",             "Hosea",            14, 28),
        BibleBook("JOL", "Joël",             "Joel",              3, 29),
        BibleBook("AMO", "Amos",             "Amos",              9, 30),
        BibleBook("OBA", "Abdias",           "Obadiah",           1, 31),
        BibleBook("JON", "Jonas",            "Jonah",             4, 32),
        BibleBook("MIC", "Michée",           "Micah",             7, 33),
        BibleBook("NAM", "Nahum",            "Nahum",             3, 34),
        BibleBook("HAB", "Habacuc",          "Habakkuk",          3, 35),
        BibleBook("ZEP", "Sophonie",         "Zephaniah",         3, 36),
        BibleBook("HAG", "Aggée",            "Haggai",            2, 37),
        BibleBook("ZEC", "Zacharie",         "Zechariah",        14, 38),
        BibleBook("MAL", "Malachie",         "Malachi",           4, 39),
        BibleBook("MAT", "Matthieu",         "Matthew",          28, 40),
        BibleBook("MRK", "Marc",             "Mark",             16, 41),
        BibleBook("LUK", "Luc",              "Luke",             24, 42),
        BibleBook("JHN", "Jean",             "John",             21, 43),
        BibleBook("ACT", "Actes",            "Acts",             28, 44),
        BibleBook("ROM", "Romains",          "Romans",           16, 45),
        BibleBook("1CO", "1 Corinthiens",    "1 Corinthians",    16, 46),
        BibleBook("2CO", "2 Corinthiens",    "2 Corinthians",    13, 47),
        BibleBook("GAL", "Galates",          "Galatians",         6, 48),
        BibleBook("EPH", "Éphésiens",        "Ephesians",         6, 49),
        BibleBook("PHP", "Philippiens",      "Philippians",       4, 50),
        BibleBook("COL", "Colossiens",       "Colossians",        4, 51),
        BibleBook("1TH", "1 Thessaloniciens","1 Thessalonians",   5, 52),
        BibleBook("2TH", "2 Thessaloniciens","2 Thessalonians",   3, 53),
        BibleBook("1TI", "1 Timothée",       "1 Timothy",         6, 54),
        BibleBook("2TI", "2 Timothée",       "2 Timothy",         4, 55),
        BibleBook("TIT", "Tite",             "Titus",             3, 56),
        BibleBook("PHM", "Philémon",         "Philemon",          1, 57),
        BibleBook("HEB", "Hébreux",          "Hebrews",          13, 58),
        BibleBook("JAS", "Jacques",          "James",             5, 59),
        BibleBook("1PE", "1 Pierre",         "1 Peter",           5, 60),
        BibleBook("2PE", "2 Pierre",         "2 Peter",           3, 61),
        BibleBook("1JN", "1 Jean",           "1 John",            5, 62),
        BibleBook("2JN", "2 Jean",           "2 John",            1, 63),
        BibleBook("3JN", "3 Jean",           "3 John",            1, 64),
        BibleBook("JUD", "Jude",             "Jude",              1, 65),
        BibleBook("REV", "Apocalypse",       "Revelation",       22, 66),
    )

    private val byId: Map<String, BibleBook> = all.associateBy { it.bookId }

    /** Returns the book with the given [bookId], or null if not found. */
    fun findById(bookId: String): BibleBook? = byId[bookId]

    /**
     * Returns the next book in canonical order after [bookId], or null if [bookId]
     * is the last book (Revelation).
     */
    fun nextBook(bookId: String): BibleBook? {
        val current = byId[bookId] ?: return null
        return all.getOrNull(current.canonicalOrder) // canonicalOrder is 1-based; index is 0-based
    }

    /**
     * Returns the previous book in canonical order before [bookId], or null if [bookId]
     * is the first book (Genesis).
     */
    fun previousBook(bookId: String): BibleBook? {
        val current = byId[bookId] ?: return null
        return all.getOrNull(current.canonicalOrder - 2)
    }
}