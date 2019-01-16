CREATE TABLE IF NOT EXISTS person (
name TEXT NOT NULL,
surname TEXT NOT NULL,
password TEXT NOT NULL,
username TEXT PRIMARY KEY,
is_active BOOL NOT NULL
) ENGINE=InnoDB ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS is_user (
id_person TEXT NOT NULL,
FOREIGN KEY (id_person) REFERENCES person(username)
) ENGINE=InnoDB ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS is_operator (
id_person TEXT NOT NULL,
is_admin  BOOL NOT NULL,
FOREIGN KEY (id_person) REFERENCES person(username)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS subscription (
id INTEGER PRIMARY KEY AUTO_INCREMENT,
id_person TEXT NOT NULL,
start_date TEXT NOT NULL,
end_date TEXT NOT NULL,
is_renewed  BOOL NOT NULL,
is_active  BOOL NOT NULL,
FOREIGN KEY (id_person) REFERENCES person(username)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS renew_subscription (
id_sub INTEGER,
start_date TEXT NOT NULL,
end_date TEXT NOT NULL,
is_renewed BOOL NOT NULL,
is_active BOOL NOT NULL,
FOREIGN KEY (id_sub) REFERENCES subscription(id)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS person (
id INTEGER PRIMARY KEY AUTO_INCREMENT,
name TEXT NOT NULL,
surname TEXT NOT NULL,
username TEXT NOT NULL,
is_active BOOL NOT NULL) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS category (
id INTEGER PRIMARY KEY AUTO_INCREMENT,
identifier TEXT NOT NULL UNIQUE,
name TEXT NOT NULL,
description TEXT,
parent TEXT,
is_subcategory  BOOL NOT NULL,
loan_duration TEXT NOT NULL,
renew_duration TEXT NOT NULL,
days_until_renew TEXT NOT NULL,
max_loans_to_person TEXT
is_active BOOL NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS book (
id INTEGER PRIMARY KEY AUTO_INCREMENT,
identifier TEXT NOT NULL UNIQUE,
publication_year TEXT,
title TEXT,
description TEXT,
authors TEXT,
language TEXT,
publisher TEXT,
genre TEXT,
page_number TEXT,
license_number INTEGER,
is_avaiable BOOL,
is_active BOOL,
category_ident TEXT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS loan (
id INTEGER PRIMARY KEY AUTO_INCREMENT,
id_person TEXT NOT NULL,
id_book TEXT NOT NULL,
start_date TEXT NOT NULL,
end_date TEXT NOT NULL,
quit_loan_date TEXT,
is_renewed  BOOL NOT NULL,
is_active  BOOL NOT NULL,
FOREIGN KEY (id_book) REFERENCES book(identifier),
FOREIGN KEY (id_person) REFERENCES person(username)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS renew_loan (
id INTEGER PRIMARY KEY AUTO_INCREMENT,
id_loan INTEGER NOT NULL,
id_book TEXT NULL,
start_date TEXT NOT NULL,
end_date TEXT NOT NULL,
quit_loan_date TEXT,
is_renewed BOOL NULL,
is_active BOOL NOT NULL,
FOREIGN KEY (id_book) REFERENCES book(identifier),
FOREIGN KEY (id_loan) REFERENCES loan(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS film (
id INTEGER PRIMARY KEY AUTO_INCREMENT,
identifier TEXT NOT NULL UNIQUE,
production_year TEXT,
title TEXT,
description TEXT,
actors TEXT,
film_maker TEXT,
language TEXT,
publisher TEXT,
genre TEXT,
duration TEXT,
license_number INTEGER,
is_avaiable BOOL,
is_active BOOL,
category_ident TEXT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS film_loan (
id INTEGER PRIMARY KEY AUTO_INCREMENT,
id_person TEXT NOT NULL,
id_film TEXT NULL,
start_date TEXT NOT NULL,
end_date TEXT NOT NULL,
quit_loan_date TEXT,
is_renewed BOOL NOT NULL,
is_active BOOL NOT NULL,
FOREIGN KEY (id_film) REFERENCES film(identifier),
FOREIGN KEY (id_person) REFERENCES person(username)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS renew_film_loan (
id INTEGER PRIMARY KEY AUTO_INCREMENT,
id_loan INTEGER NOT NULL,
id_film TEXT NOT NULL,
start_date TEXT NOT NULL,
end_date TEXT NOT NULL,
quit_loan_date TEXT,
is_renewed BOOL NULL,
is_active BOOL NOT NULL,
FOREIGN KEY (id_film) REFERENCES film(identifier),
FOREIGN KEY (id_loan) REFERENCES loan(id)
) ENGINE=InnoDB;
