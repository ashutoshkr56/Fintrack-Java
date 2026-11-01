
CREATE DATABASE IF NOT EXISTS fintrack_db;

USE fintrack_db;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NULL,
    name VARCHAR(100) NOT NULL,
    type ENUM('INCOME', 'EXPENSE') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,

    UNIQUE KEY unique_user_category (user_id, name, type)
);

CREATE TABLE IF NOT EXISTS transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    type ENUM('INCOME', 'EXPENSE') NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    description VARCHAR(255),
    transaction_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,

    FOREIGN KEY (category_id) REFERENCES categories(id),

    CHECK (amount > 0),

    INDEX idx_user_date (user_id, transaction_date)
);

CREATE TABLE IF NOT EXISTS budgets (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    budget_month DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,

    FOREIGN KEY (category_id) REFERENCES categories(id),

    CHECK (amount > 0),

    UNIQUE KEY unique_monthly_budget
        (user_id, category_id, budget_month)
);

-- Seed default global categories (where user_id is NULL)
INSERT INTO categories (user_id, name, type)
SELECT * FROM (
    SELECT NULL AS user_id, 'Salary' AS name, 'INCOME' AS type UNION ALL
    SELECT NULL, 'Freelance', 'INCOME' UNION ALL
    SELECT NULL, 'Investments', 'INCOME' UNION ALL
    SELECT NULL, 'Groceries', 'EXPENSE' UNION ALL
    SELECT NULL, 'Rent & Housing', 'EXPENSE' UNION ALL
    SELECT NULL, 'Utilities', 'EXPENSE' UNION ALL
    SELECT NULL, 'Transportation', 'EXPENSE' UNION ALL
    SELECT NULL, 'Dining Out', 'EXPENSE' UNION ALL
    SELECT NULL, 'Entertainment', 'EXPENSE' UNION ALL
    SELECT NULL, 'Healthcare', 'EXPENSE'
) AS seed
WHERE NOT EXISTS (
    SELECT 1 FROM categories c
    WHERE c.user_id IS NULL AND c.name = seed.name AND c.type = seed.type
);
