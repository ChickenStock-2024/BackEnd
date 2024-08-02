-- INSERT INTO member (member_id, nickname, email, password, point) VALUES
--                                                                      (1, 'SampleUser1', 'sampleuser1@mail.com', 'password123', 0),
--                                                                      (2, 'SampleUser2', 'sampleuser2@mail.com', 'password123', 0),
--                                                                      (3, 'SampleUser3', 'sampleuser3@mail.com', 'password123', 0),
--                                                                      (4, 'SampleUser4', 'sampleuser4@mail.com', 'password123', 0),
--                                                                      (5, 'SampleUser5', 'sampleuser5@mail.com', 'password123', 0),
--                                                                      (6, 'SampleUser6', 'sampleuser6@mail.com', 'password123', 0),
--                                                                      (7, 'SampleUser7', 'sampleuser7@mail.com', 'password123', 0),
--                                                                      (8, 'SampleUser8', 'sampleuser8@mail.com', 'password123', 0),
--                                                                      (9, 'SampleUser9', 'sampleuser9@mail.com', 'password123', 0),
--                                                                      (10, 'SampleUser10', 'sampleuser10@mail.com', 'password123', 0);
-- INSERT INTO competition (competition_id, title, start_at, end_at, total_people) VALUES
--                                                                                     (1, 'Sample Competition 1', '2024-07-01 08:00:00', '2024-07-31 17:00:00', 10),
--                                                                                     (2, 'Sample Competition 2', '2024-08-01 08:00:00', '2024-08-31 17:00:00', 15),
--                                                                                     (3, 'Sample Competition 3', '2024-09-01 08:00:00', '2024-09-30 17:00:00', 20),
--                                                                                     (4, 'Sample Competition 4', '2024-10-01 08:00:00', '2024-10-31 17:00:00', 25),
--                                                                                     (5, 'Sample Competition 5', '2024-11-01 08:00:00', '2024-11-30 17:00:00', 30),
--                                                                                     (6, 'Sample Competition 6', '2024-12-01 08:00:00', '2024-12-31 17:00:00', 35),
--                                                                                     (7, 'Sample Competition 7', '2025-01-01 08:00:00', '2025-01-31 17:00:00', 40),
--                                                                                     (8, 'Sample Competition 8', '2025-02-01 08:00:00', '2025-02-28 17:00:00', 45),
--                                                                                     (9, 'Sample Competition 9', '2025-03-01 08:00:00', '2025-03-31 17:00:00', 50),
--                                                                                     (10, 'Sample Competition 10', '2025-04-01 08:00:00', '2025-04-30 17:00:00', 55);
-- INSERT INTO account (account_id, member_id, competition_id, balance, ranking, rating_change, created_at, updated_at) VALUES
--                                                                                                                          (1, 1, 1, 50000000, 1, 10, '2024-07-01 00:00:00', '2024-07-01 00:00:00'),
--                                                                                                                          (2, 2, 2, 50000000, 2, 8, '2024-07-01 00:00:00', '2024-07-01 00:00:00'),
--                                                                                                                          (3, 3, 3, 50000000, 3, 5, '2024-07-01 00:00:00', '2024-07-01 00:00:00'),
--                                                                                                                          (4, 4, 4, 50000000, 4, 12, '2024-07-01 00:00:00', '2024-07-01 00:00:00'),
--                                                                                                                          (5, 5, 5, 50000000, 5, 9, '2024-07-01 00:00:00', '2024-07-01 00:00:00'),
--                                                                                                                          (6, 6, 6, 50000000, 6, 7, '2024-07-01 00:00:00', '2024-07-01 00:00:00'),
--                                                                                                                          (7, 7, 7, 50000000, 7, 11, '2024-07-01 00:00:00', '2024-07-01 00:00:00'),
--                                                                                                                          (8, 8, 8, 50000000, 8, 10, '2024-07-01 00:00:00', '2024-07-01 00:00:00'),
--                                                                                                                          (9, 9, 9, 50000000, 9, 6, '2024-07-01 00:00:00', '2024-07-01 00:00:00'),
--                                                                                                                          (10, 10, 10, 50000000, 10, 15, '2024-07-01 00:00:00', '2024-07-01 00:00:00');
-- INSERT INTO company (company_id, name, code, status, created_at, updated_at) VALUES
--                                                                                  (1, 'Chicken Delight', 'CHD01', 'LISTED', '2024-07-01 00:00:00', '2024-07-01 00:00:00'),
--                                                                                  (2, 'Tasty Chickens Co.', 'TCC02', 'LISTED', '2024-07-01 00:00:00', '2024-07-01 00:00:00'),
--                                                                                  (3, 'Happy Farms', 'HFR03', 'LISTED', '2024-07-01 00:00:00', '2024-07-01 00:00:00'),
--                                                                                  (4, 'Golden Eggs Ltd.', 'GEL04', 'LISTED', '2024-07-01 00:00:00', '2024-07-01 00:00:00'),
--                                                                                  (5, 'Rooster & Hen Inc.', 'RHI05', 'LISTED', '2024-07-01 00:00:00', '2024-07-01 00:00:00'),
--                                                                                  (6, 'Fresh Poultry Corp.', 'FPC06', 'LISTED', '2024-07-01 00:00:00', '2024-07-01 00:00:00'),
--                                                                                  (7, 'Cluckers Enterprises', 'CLE07', 'LISTED', '2024-07-01 00:00:00', '2024-07-01 00:00:00'),
--                                                                                  (8, 'Feathered Friends LLC', 'FFL08', 'LISTED', '2024-07-01 00:00:00', '2024-07-01 00:00:00'),
--                                                                                  (9, 'Poultry Palace', 'PPL09', 'LISTED', '2024-07-01 00:00:00', '2024-07-01 00:00:00'),
--                                                                                  (10, 'Eggcellent Farms', 'EGF10', 'LISTED', '2024-07-01 00:00:00', '2024-07-01 00:00:00');
-- Member 데이터 삽입
INSERT INTO member (nickname, email, password, point) VALUES
                                                          ('user1', 'user1@example.com', 'password1', 0),
                                                          ('user2', 'user2@example.com', 'password2', 0),
                                                          ('user3', 'user3@example.com', 'password3', 0),
                                                          ('user4', 'user4@example.com', 'password4', 0),
                                                          ('user5', 'user5@example.com', 'password5', 0),
                                                          ('user6', 'user6@example.com', 'password6', 0),
                                                          ('user7', 'user7@example.com', 'password7', 0),
                                                          ('user8', 'user8@example.com', 'password8', 0),
                                                          ('user9', 'user9@example.com', 'password9', 0),
                                                          ('user10', 'user10@example.com', 'password10', 0);

-- Company 데이터 삽입
INSERT INTO company (name, code, status) VALUES
                                             ('Company A', 'C001', 'LISTED'),
                                             ('Company B', 'C002', 'LISTED'),
                                             ('Company C', 'C003', 'LISTED'),
                                             ('Company D', 'C004', 'LISTED'),
                                             ('Company E', 'C005', 'LISTED'),
                                             ('Company F', 'C006', 'LISTED'),
                                             ('Company G', 'C007', 'LISTED'),
                                             ('Company H', 'C008', 'LISTED'),
                                             ('Company I', 'C009', 'LISTED'),
                                             ('Company J', 'C010', 'LISTED');

-- Competition 데이터 삽입
INSERT INTO competition (title, start_at, end_at, total_people) VALUES
                                                                    ('Competition 1', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 10),
                                                                    ('Competition 2', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 10);

-- Account 데이터 삽입
-- 멤버마다 두 개의 계좌를 생성하고, 한 대회에 참가한 계좌를 분배합니다.
INSERT INTO account (member_id, competition_id, balance, ranking, rating_change) VALUES
                                                                                     (1, 1, 50000000, 0, 0),
                                                                                     (1, 2, 50000000, 0, 0),
                                                                                     (2, 1, 50000000, 0, 0),
                                                                                     (2, 2, 50000000, 0, 0),
                                                                                     (3, 1, 50000000, 0, 0),
                                                                                     (3, 2, 50000000, 0, 0),
                                                                                     (4, 1, 50000000, 0, 0),
                                                                                     (4, 2, 50000000, 0, 0),
                                                                                     (5, 1, 50000000, 0, 0),
                                                                                     (5, 2, 50000000, 0, 0),
                                                                                     (6, 1, 50000000, 0, 0),
                                                                                     (6, 2, 50000000, 0, 0),
                                                                                     (7, 1, 50000000, 0, 0),
                                                                                     (7, 2, 50000000, 0, 0),
                                                                                     (8, 1, 50000000, 0, 0),
                                                                                     (8, 2, 50000000, 0, 0),
                                                                                     (9, 1, 50000000, 0, 0),
                                                                                     (9, 2, 50000000, 0, 0),
                                                                                     (10, 1, 50000000, 0, 0),
                                                                                     (10, 2, 50000000, 0, 0);

-- History 데이터 삽입 예시 (원하는 대로 추가)
-- account_id와 company_id는 위의 데이터 삽입 결과에 따라 적절히 매칭하여 사용
-- History 데이터 삽입
INSERT INTO history (account_id, company_id, price, volume, status) VALUES
-- 계좌 1
(1, 1, 1000, 10, '지정가매수요청'),
(1, 2, 1500, 15, '지정가매도요청'),
(1, 3, 2000, 20, '시장가매수요청'),
(1, 4, 2500, 25, '시장가매도요청'),
(1, 5, 3000, 30, '지정가매수요청'),
(1, 6, 3500, 35, '지정가매도요청'),

-- 계좌 2
(2, 1, 1100, 11, '지정가매수요청'),
(2, 2, 1600, 16, '지정가매도요청'),
(2, 3, 2100, 21, '시장가매수요청'),
(2, 4, 2600, 26, '시장가매도요청'),
(2, 5, 3100, 31, '지정가매수요청'),
(2, 6, 3600, 36, '지정가매도요청'),

-- 계좌 3
(3, 1, 1200, 12, '지정가매수요청'),
(3, 2, 1700, 17, '지정가매도요청'),
(3, 3, 2200, 22, '시장가매수요청'),
(3, 4, 2700, 27, '시장가매도요청'),
(3, 5, 3200, 32, '지정가매수요청'),
(3, 6, 3700, 37, '지정가매도요청'),

-- 계좌 4
(4, 1, 1300, 13, '지정가매수요청'),
(4, 2, 1800, 18, '지정가매도요청'),
(4, 3, 2300, 23, '시장가매수요청'),
(4, 4, 2800, 28, '시장가매도요청'),
(4, 5, 3300, 33, '지정가매수요청'),
(4, 6, 3800, 38, '지정가매도요청'),

-- 계좌 5
(5, 1, 1400, 14, '지정가매수요청'),
(5, 2, 1900, 19, '지정가매도요청'),
(5, 3, 2400, 24, '시장가매수요청'),
(5, 4, 2900, 29, '시장가매도요청'),
(5, 5, 3400, 34, '지정가매수요청'),
(5, 6, 3900, 39, '지정가매도요청'),

-- 계좌 6
(6, 1, 1500, 15, '지정가매수요청'),
(6, 2, 2000, 20, '지정가매도요청'),
(6, 3, 2500, 25, '시장가매수요청'),
(6, 4, 3000, 30, '시장가매도요청'),
(6, 5, 3500, 35, '지정가매수요청'),
(6, 6, 4000, 40, '지정가매도요청'),

-- 계좌 7
(7, 1, 1600, 16, '지정가매수요청'),
(7, 2, 2100, 21, '지정가매도요청'),
(7, 3, 2600, 26, '시장가매수요청'),
(7, 4, 3100, 31, '시장가매도요청'),
(7, 5, 3600, 36, '지정가매수요청'),
(7, 6, 4100, 41, '지정가매도요청'),

-- 계좌 8
(8, 1, 1700, 17, '지정가매수요청'),
(8, 2, 2200, 22, '지정가매도요청'),
(8, 3, 2700, 27, '시장가매수요청'),
(8, 4, 3200, 32, '시장가매도요청'),
(8, 5, 3700, 37, '지정가매수요청'),
(8, 6, 4200, 42, '지정가매도요청'),

-- 계좌 9
(9, 1, 1800, 18, '지정가매수요청'),
(9, 2, 2300, 23, '지정가매도요청'),
(9, 3, 2800, 28, '시장가매수요청'),
(9, 4, 3300, 33, '시장가매도요청'),
(9, 5, 3800, 38, '지정가매수요청'),
(9, 6, 4300, 43, '지정가매도요청'),

-- 계좌 10
(10, 1, 1900, 19, '지정가매수요청'),
(10, 2, 2400, 24, '지정가매도요청'),
(10, 3, 2900, 29, '시장가매수요청'),
(10, 4, 3400, 34, '시장가매도요청'),
(10, 5, 3900, 39, '지정가매수요청'),
(10, 6, 4400, 44, '지정가매도요청'),

-- 계좌 11
(11, 1, 2000, 20, '지정가매수요청'),
(11, 2, 2500, 25, '지정가매도요청'),
(11, 3, 3000, 30, '시장가매수요청'),
(11, 4, 3500, 35, '시장가매도요청'),
(11, 5, 4000, 40, '지정가매수요청'),
(11, 6, 4500, 45, '지정가매도요청'),

-- 계좌 12
(12, 1, 2100, 21, '지정가매수요청'),
(12, 2, 2600, 26, '지정가매도요청'),
(12, 3, 3100, 31, '시장가매수요청'),
(12, 4, 3600, 36, '시장가매도요청'),
(12, 5, 4100, 41, '지정가매수요청'),
(12, 6, 4600, 46, '지정가매도요청'),

-- 계좌 13
(13, 1, 2200, 22, '지정가매수요청'),
(13, 2, 2700, 27, '지정가매도요청'),
(13, 3, 3200, 32, '시장가매수요청'),
(13, 4, 3700, 37, '시장가매도요청'),
(13, 5, 4200, 42, '지정가매수요청'),
(13, 6, 4700, 47, '지정가매도요청'),

-- 계좌 14
(14, 1, 2300, 23, '지정가매수요청'),
(14, 2, 2800, 28, '지정가매도요청'),
(14, 3, 3300, 33, '시장가매수요청'),
(14, 4, 3800, 38, '시장가매도요청'),
(14, 5, 4300, 43, '지정가매수요청'),
(14, 6, 4800, 48, '지정가매도요청'),

-- 계좌 15
(15, 1, 2400, 24, '지정가매수요청'),
(15, 2, 2900, 29, '지정가매도요청'),
(15, 3, 3400, 34, '시장가매수요청'),
(15, 4, 3900, 39, '시장가매도요청'),
(15, 5, 4400, 44, '지정가매수요청'),
(15, 6, 4900, 49, '지정가매도요청'),

-- 계좌 16
(16, 1, 2500, 25, '지정가매수요청'),
(16, 2, 3000, 30, '지정가매도요청'),
(16, 3, 3500, 35, '시장가매수요청'),
(16, 4, 4000, 40, '시장가매도요청'),
(16, 5, 4500, 45, '지정가매수요청'),
(16, 6, 5000, 50, '지정가매도요청'),

-- 계좌 17
(17, 1, 2600, 26, '지정가매수요청'),
(17, 2, 3100, 31, '지정가매도요청'),
(17, 3, 3600, 36, '시장가매수요청'),
(17, 4, 4100, 41, '시장가매도요청'),
(17, 5, 4600, 46, '지정가매수요청'),
(17, 6, 5100, 51, '지정가매도요청'),

-- 계좌 18
(18, 1, 2700, 27, '지정가매수요청'),
(18, 2, 3200, 32, '지정가매도요청'),
(18, 3, 3700, 37, '시장가매수요청'),
(18, 4, 4200, 42, '시장가매도요청'),
(18, 5, 4700, 47, '지정가매수요청'),
(18, 6, 5200, 52, '지정가매도요청'),

-- 계좌 19
(19, 1, 2800, 28, '지정가매수요청'),
(19, 2, 3300, 33, '지정가매도요청'),
(19, 3, 3800, 38, '시장가매수요청'),
(19, 4, 4300, 43, '시장가매도요청'),
(19, 5, 4800, 48, '지정가매수요청'),
(19, 6, 5300, 53, '지정가매도요청'),

-- 계좌 20
(20, 1, 2900, 29, '지정가매수요청'),
(20, 2, 3400, 34, '지정가매도요청'),
(20, 3, 3900, 39, '시장가매수요청'),
(20, 4, 4400, 44, '시장가매도요청'),
(20, 5, 4900, 49, '지정가매수요청'),
(20, 6, 5400, 54, '지정가매도요청');
