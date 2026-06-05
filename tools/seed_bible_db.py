#!/usr/bin/env python3
"""
Builds bible_reader.db from ebible.org "readaloud" zip archives.

Usage:
    python3 tools/seed_bible_db.py

Sources (fetched automatically during Feature a setup):
    /tmp/kjv_raw/   -- eng-kjv_*_read.txt files
    /tmp/lsg_raw/   -- fraLSG_*_read.txt files

Output: tools/bible_reader.db
    Copy to:
        composeApp/src/androidMain/assets/bible_reader.db
        iosApp/iosApp/bible_reader.db
    Then add bible_reader.db to Xcode target -> Copy Bundle Resources.
"""
import glob
import pathlib
import re
import sqlite3

# 66 Protestant canon book IDs — must match CanonicalBooks.kt
CANONICAL_BOOKS = {
    "GEN", "EXO", "LEV", "NUM", "DEU", "JOS", "JDG", "RUT",
    "1SA", "2SA", "1KI", "2KI", "1CH", "2CH", "EZR", "NEH", "EST",
    "JOB", "PSA", "PRO", "ECC", "SNG", "ISA", "JER", "LAM", "EZK",
    "DAN", "HOS", "JOL", "AMO", "OBA", "JON", "MIC", "NAM", "HAB",
    "ZEP", "HAG", "ZEC", "MAL",
    "MAT", "MRK", "LUK", "JHN", "ACT", "ROM", "1CO", "2CO", "GAL",
    "EPH", "PHP", "COL", "1TH", "2TH", "1TI", "2TI", "TIT", "PHM",
    "HEB", "JAS", "1PE", "2PE", "1JN", "2JN", "3JN", "JUD", "REV",
}

DB_PATH = pathlib.Path(__file__).parent / "bible_reader.db"

SOURCES = [
    ("KJV", "/tmp/kjv_raw", "eng-kjv_*_read.txt"),
    ("LSG", "/tmp/lsg_raw", "fraLSG_*_read.txt"),
]

# Filename pattern: prefix_NNN_BOOKID_CHAP_read.txt
FILE_RE = re.compile(r"_(\d+)_([A-Z0-9]+)_(\d+)_read\.txt$")


def parse_chapter_file(path: pathlib.Path) -> list:
    """
    Returns ordered verse strings from an ebible readaloud chapter file.
    Format: line 1 = book title, line 2 = chapter header, rest = one verse per line.
    Pilcrow signs at line start are stripped; empty lines skipped.
    """
    lines = path.read_text(encoding="utf-8-sig").splitlines()
    verses = []
    for line in lines[2:]:  # skip book title + chapter header
        text = line.strip().lstrip("¶").strip()
        if text:
            verses.append(text)
    return verses


def build_db() -> None:
    if DB_PATH.exists():
        DB_PATH.unlink()

    con = sqlite3.connect(DB_PATH)
    cur = con.cursor()
    cur.executescript("""
        CREATE TABLE BibleVerse (
            translation TEXT NOT NULL,
            book_id     TEXT NOT NULL,
            chapter     INTEGER NOT NULL,
            verse       INTEGER NOT NULL,
            text        TEXT NOT NULL,
            PRIMARY KEY (translation, book_id, chapter, verse)
        );
        CREATE INDEX idx_bible_verse_chapter
            ON BibleVerse (translation, book_id, chapter);
    """)

    for translation_key, source_dir, glob_pattern in SOURCES:
        files = sorted(glob.glob(f"{source_dir}/{glob_pattern}"))
        rows = []
        skipped = 0

        for filepath in files:
            m = FILE_RE.search(filepath)
            if not m:
                continue
            book_id = m.group(2)
            chapter = int(m.group(3))

            if book_id not in CANONICAL_BOOKS:
                skipped += 1
                continue

            verses = parse_chapter_file(pathlib.Path(filepath))
            for verse_num, text in enumerate(verses, start=1):
                rows.append((translation_key, book_id, chapter, verse_num, text))

        cur.executemany("INSERT INTO BibleVerse VALUES (?,?,?,?,?)", rows)
        con.commit()
        size_mb = DB_PATH.stat().st_size / 1_000_000
        print(f"{translation_key}: {len(rows):,} verses inserted "
              f"({skipped} apocrypha files skipped) -- db {size_mb:.1f} MB so far")

    cur.execute("SELECT COUNT(*) FROM BibleVerse WHERE translation='KJV'")
    kjv_count = cur.fetchone()[0]
    cur.execute("SELECT COUNT(*) FROM BibleVerse WHERE translation='LSG'")
    lsg_count = cur.fetchone()[0]
    cur.execute("SELECT COUNT(DISTINCT book_id) FROM BibleVerse WHERE translation='KJV'")
    kjv_books = cur.fetchone()[0]
    con.close()

    print(f"\nSanity check:")
    print(f"  KJV: {kjv_count:,} verses across {kjv_books} books (expected ~31,102 / 66)")
    print(f"  LSG: {lsg_count:,} verses (expected ~31,000)")
    size_mb = DB_PATH.stat().st_size / 1_000_000
    print(f"\nOutput: {DB_PATH} ({size_mb:.1f} MB)")
    print("\nNext steps:")
    print(f"  cp {DB_PATH} composeApp/src/androidMain/assets/bible_reader.db")
    print(f"  cp {DB_PATH} iosApp/iosApp/bible_reader.db")
    print("  Add bible_reader.db to Xcode target -> Copy Bundle Resources")


if __name__ == "__main__":
    build_db()