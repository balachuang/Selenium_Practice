spring:
  profiles:
    active: cartoonmad

browser:
  driver: Chrome
  minimize: false
  ## 目前無法真的 hide, 要再研究.
  hide: false

spider:
  ## 目前可以抓的 comic-web: 8-comic / cartoonmad / apexmh
  comic-web: cartoonmad
  ## radom-interval 設定值:
  ##   大於 0 : 固定每下載一張圖片後暫停秒數
  ##   等於 0 : 不暫停, 連續下載圖片
  ##   小於 0 : 每下載一張圖片後隨機暫停, 暫停秒數為 0 ~ ABS(設定值)
  random-interval: 0
  local-save:
    base-path: D:\MyDownload\Images\Comic
    save-to-sub-folder: true


---
# 無限動漫 https://www.comicabc.com/
spring:
  profiles: 8-comic

web:
  url:
    temp-login: https://v.comicabc.com/member/login.aspx
    temp-cover: https://www.comicabc.com/html/{{comic-id}}.html
  username: Balachuang
  password: duperman0313

comic:
  name: 咒術回戰
  id: 15790
  chap-str: 3
  chap-end: 3
  ## page-str <= 0 : download from the first page
  ## page-end <= 0 : download to the last page
  page-str: 5
  page-end: 6


---
# 動漫狂 https://www.cartoonmad.com/
spring:
  profiles: cartoonmad

web:
  url:
    temp-login: 
    temp-cover: https://www.cartoonmad.com/comic/{{comic-id}}.html
  username: 
  password: 

comic:
  name: 迷宮飯
  id: 4455
  chap-str: 3
  chap-end: 3
  page-str: 5
  page-end: 6


---
# 紳士漫畫 禁漫天堂 https://www.apexmh.com/
spring:
  profiles: apexmh

web:
  url:
    temp-login: 
    temp-cover: https://www.apexmh.com/comic/{{comic-id}}.html
  username: 
  password: 

comic:
  name: 芙莉蓮自拍
  id: 60930
  chap-str: 3
  chap-end: 3
  page-str: 5
  page-end: 6


# 動漫戲說 https://comic.acgn.cc/
# 開車漫畫 https://18p.fun/
