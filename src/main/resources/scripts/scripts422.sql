CREATE TABLE people (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        age INT NOT NULL CHECK ( age >17 ),
                        has_license BOOLEAN NOT NULL
);

CREATE TABLE cars (
                      id SERIAL PRIMARY KEY,
                      brand VARCHAR(100) NOT NULL,
                      model VARCHAR(100) NOT NULL,
                      price NUMERIC(10, 2) NOT NULL
);

CREATE TABLE people_cars (
                             person_id INT REFERENCES people(id) ON DELETE CASCADE,
                             car_id INT REFERENCES cars(id) ON DELETE CASCADE,
                             PRIMARY KEY (person_id, car_id),
--     Добавил проверку, что если нет прав - то пользоваться машиной запрещено, а то совсем скучно )
                             CHECK (
                                 EXISTS (
                                     SELECT 1
                                     FROM people
                                     WHERE id = person_id AND has_license = TRUE
                                 )
                                 )
);