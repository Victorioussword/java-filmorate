
-- Таблицы для хранения Film

create TABLE IF NOT EXISTS mpa (
id integer primary key NOT null,  -- Добавил NOT null
name varchar (20)  NOT null  -- Добавил NOT null
);

create table if not exists genre (
id integer primary key NOT null,  -- Добавил NOT null
name varchar (50)  NOT null  -- Добавил NOT null
);

create TABLE IF NOT EXISTS film (
id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name VARCHAR(50) ,
description VARCHAR(200),
realise_date DATE,
duration INTEGER,
mpa_id INTEGER ,
FOREIGN KEY (mpa_id) REFERENCES mpa (id)
);

create TABLE IF NOT EXISTS film_genre (
film_id INTEGER NOT null,  -- Добавил NOT null
genre_id INTEGER NOT null, -- Добавил NOT null
FOREIGN KEY (film_id) REFERENCES film (id),
FOREIGN KEY (genre_id) REFERENCES genre (id),
PRIMARY KEY (FILM_ID, GENRE_ID)
);

--Таблицы для хранения User

create TABLE IF NOT EXISTS USERS (
ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
EMAIL varchar (50) NOT null,
BIRTHDAY date NOT null,
LOGIN varchar (50) NOT null,
NAME varchar (50)
);
create unique index if not exists USER_EMAIL_UINDEX on USERS (email);
create unique index if not exists USER_LOGIN_UINDEX on USERS (login);


create TABLE IF NOT EXISTS LIKES (
FILM_ID INTEGER NOT null,   -- Добавил NOT null
USER_ID INTEGER NOT null,   -- Добавил NOT null
FOREIGN KEY (FILM_ID)  REFERENCES FILM (ID),
FOREIGN KEY (USER_ID)  REFERENCES USERS (ID),
PRIMARY KEY (user_id,film_id)
);

create TABLE IF NOT EXISTS FRIENDSHIP (
USER_ID INTEGER NOT null,  -- Добавил NOT null
FRIEND_ID INTEGER NOT null,  -- Добавил NOT null
FOREIGN KEY (USER_ID) REFERENCES USERS(ID),
FOREIGN KEY (FRIEND_ID) REFERENCES USERS(ID),
PRIMARY KEY (USER_ID, FRIEND_ID)
);