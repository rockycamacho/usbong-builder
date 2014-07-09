TRUNCATE TABLE Screens;

ALTER TABLE Screens ADD COLUMN ScreenType TEXT;

INSERT INTO Screens
(
    Id,
    Name,
    Utree,
    ScreenType,
    Display
)
VALUES
(
    1,
    "Start",
    1,
    "TEXT_DISPLAY"
    "Hello World"
);

INSERT INTO Screens
(
    Id,
    Name,
    Utree,
    ScreenType,
    Display
)
VALUES
(
    2,
    "Second page",
    1,
    "TEXT_DISPLAY"
    "Second page"
);
