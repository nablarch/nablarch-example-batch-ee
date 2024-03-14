OPTIONS (
DIRECT = TRUE
)
LOAD DATA
CHARACTERSET JA16SJISTILDE
INFILE '' "str '\r\n'"
TRUNCATE
PRESERVE BLANKS
INTO TABLE ZIP_CODE_DATA_WORK
FIELDS
TERMINATED BY ','
OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
LINE_NUMBER RECNUM,
LOCAL_GOVERNMENT_CODE,
ZIP_CODE_5DIGIT,
ZIP_CODE_7DIGIT,
PREFECTURE_KANA,
CITY_KANA,
ADDRESS_KANA,
PREFECTURE_KANJI,
CITY_KANJI,
ADDRESS_KANJI,
MULTIPLE_ZIP_CODES,
NUMBERED_EVERY_KOAZA,
ADDRESS_WITH_CHOME,
MULTIPLE_ADDRESS,
UPDATE_DATA,
UPDATE_DATA_REASON
)