CREATE TABLE INCLINOMETER (
    DVALUE1 float,
    DVALUE2 float,
    TICKS BIGINT,
    DEVICE_ID varchar(32),
    ID INTEGER NOT NULL,
    PRIMARY KEY (ID)
) ORGANIZE BY ROW;