# Script Spider

## 目錄
1. [基本說明](#基本說明)
1. [語法總覽](#語法總覽)
1. [指令說明](#指令說明)
   - [輔助指令](#輔助指令)
   - [瀏覽器相關指令](#瀏覽器相關指令)
   - [網頁元件操作指令](#網頁元件操作指令)
   - [流程控制指令](#流程控制指令)


## 基本說明
Script Spider 是一個以 Selenium 為基礎的網路爬蟲程式. 主要特點是可以透過 Script 來控制 Spider 行為, 不需要每換一個網站就要改一次程式.

## 語法總覽
Script 為 UTF-8 格式的純文字檔, 每行都是一個獨立的指令. 開頭為 --- 表示為註解, 可以使用空格及 Tab 來內縮以方便閱讀. 也可以用空白行來分隔不同區塊以增加 Script 的可讀性. 所有的指令都有中英文版本, 可參考下面範例.

以下為一段簡短 Script 範例及對應說明:

```
-- define parameters
Define testUrl https://www.momoshop.com.tw/main/Main.jsp

-- MOMO
Navigate $(testUrl)
Click #keyword
Input 測試  試紙
Click #topSchFrm button

-- Google
瀏覽網頁 https://www.google.com.tw/
點擊元件 textarea[name="q"]
輸入文字 Test
```

說明
| Script                                                       | 說明                                         |
|--------------------------------------------------------------|----------------------------------------------|
| --- define parameters                                        | 註解                                         |
| Define testUrl https://www.momoshop.com.tw/main/Main.jsp     | 定義變數                                      |
|                                                              | 空白行                                       |
| --- MOMO                                                     | 註解                                         |
| Navigate $(testUrl)                                          | 瀏覽網頁                                      |
| Click #keyword                                               | 使用 CSS Selector 搜尋元件並點擊               |
| Input 測試 試紙                                               | 輸入文字: "測試 試紙"                          |
|                                                              | 空白行                                        |
| --- Google                                                   | 註解                                         |
| 瀏覽網頁 https://www.google.com.tw/                           | 瀏覽網頁 (使用中文指令)                        |
| 點擊元件 textarea[name="q"]                                   | 使用 CSS Selector 搜尋元件並點擊 (使用中文指令) |
| 輸入文字 Test                                                 | 輸入 Test 文字 (使用中文指令)                  |
```
PS. 詳細 Script 範例可以參考 8Comic_eng.script / 8Comic_cht.script
```


## 指令說明
以下列出目前所有支援的指令:

### 輔助指令

- 註解
  - 指令格式:
    - ---
  - 指令說明:
    - 所有以 --- 開頭的都是註解
    - --- 之前可以有空白, 但不支援在指令後增註解, 如下例:
      - Navigate https://url --- Error, Cannot add remark after command

- 定義變數
  - 指令格式:
    - Define {key} {value}
    - 定義變數 {key} {value}
  - 指令說明:
    - 定義變數供之後使用
    - {key} 變數名稱, 不可使用空白字元, 也不可使用其他變數.
    - {value} 變數內容, 前後不需要使用雙引號, 如果有加雙引號的話, 會視為變數內容. 可以直接輸入內容, 也可以用其他已經定義過的變數.
    - 變數定義成功後, 可以用 $(key) 來使用
  - 特別說明:
    - 除了自定變數, 目前還有下列的預設變數可以使用:
      - ${loop}: 在 Loop 迴圈中取得目前 Loop Index, 如果不在迴圈中, 會直接忽略.
      - ${prevText}: 取得前一個指令擷取的內容. 如果之前沒有執行過任何一個會擷取字串的指令, 則內容為空字串.

- 用 Regular Express 剖析字串並擷取內容
  - 指令格式:
    - ParseText {text} {pattern}
    - 剖析字串 {text} {pattern}
  - 指令說明:
    - 使用 Regular Express 剖析字串
    - {text} 要被剖析的字串, 如果字串包含空白, 要用雙引號包住.
    - {pattern} 用來剖析字串的 Pattern. , 如果包含空白, 要用雙引號包住. 可以使用 (?<target>) 來指明要擷取的內容.
    - 如果 {pattern} 沒有指定 <target>, 則會擷取第一個符合 Pattern 的 group (?:) 內容.
    - 以下為範例及說明:
      - ParseText "Page: 3/10" "(?<target>\d+)/"
        - 從 "Page: 3/10" 中擷取 "3"
      - ParseText "Page: 3/10" /(\d+)
        - 從 "Page: 3/10" 中擷取 "10"

- 暫停指令執行
  - 指令格式:
    - Sleep {duration}
    - 中場休息 {duration}
  - 指令說明:
    - 休息一段時間後, 再繼續執行下一個指令.
    - {duration} 要暫停的秒數. 如果秒數為負數, 則會隨機暫停一段以其數值為上限的秒數.

### 瀏覽器相關指令

- 瀏覽網頁
  - 指令格式:
    - Navigate {url}
    - 瀏覽網頁 {url}
  - 指令說明:
    - 直接輸入網址瀏覽特定網頁
    - {url} 為網址, 可使用變數, 不可有空挌.

- 切換頁籤
  - 指令格式:
    - SwitchTab {tab-index}
    - 切換頁籤 {tab-index}
  - 指令說明:
    - 切換到某個頁籤
    - 若 {tab-index} 為 First, 切換到最左邊的頁籤, 作用等於輸入 0.
    - 若 {tab-index} 為 Latest, 切換到最右邊的頁籤, 作用等於輸入任意負數.
    - 若 {tab-index} 為大於等於 0 的數字, 則表示一個從 0 開始的頁籤編號.
    - 若 {tab-index} 為小於 0 的數字, 則表示切換到最右邊的頁籤, 作用等於輸入 Latest.

- 關閉頁籤
  - 指令格式:
    - CloseTab {tab-index}
    - 關閉頁籤 {tab-index}
  - 指令說明:
    - 關閉到某個頁籤
    - {tab-index} 為頁籤編號, 從 0 開始. 如果關閉的頁籤編號為 0, 會關閉整個瀏覽器.

### 網頁元件操作指令

- 搜尋元件
  - 指令格式:
    - Find {css-selector}
    - 搜尋元件 {css-selector}
  - 指令說明:
    - 用 CSS Selector 搜尋某個元件
    - {css-selector} 為 CSS Selector.
    - 若輸入的 CSS Selector 有對應到某個元件, 則該元件會被記錄起來.
    - 若輸入的 CSS Selector 對應到超過一個元件, 則只記錄第一個, 要查詢其他元件需自行調整 CSS Selector.

- 取得元件本身或元件內參數
  - 指令格式:
    - Get {css-selector} {attribute-name}
    - 取得元件 {css-selector} {attribute-name}
  - 指令說明:
    - 用 CSS Selector 搜尋某個元件後, 取得 Attribute 內容
    - {css-selector} 為 CSS Selector, {attribute-name} 為要回傳的 Attribute 名稱.
    - 若輸入的 CSS Selector 有對應到某個元件, 則該元件的 {attribute-name} 會被記錄為文字資料, 之後可以用 ${prevText} 取得.
    - 若輸入的 CSS Selector 對應到超過一個元件, 則只記錄第一個.
    - {attribute-name} 可使用所有出現在該元件的 Attribute, 包含 innerText, innerHtml 等.

- 搜尋元件並點擊
  - 指令格式:
    - Click {css-selector}
    - 點擊元件 {css-selector}
  - 指令說明:
    - 用 CSS Selector 搜尋某個元件後, 點擊該元件. (本行為和 Focus 有相同效果)
    - {css-selector} 為 CSS Selector.
    - 若輸入的 CSS Selector 有對應到某個元件, 則該元件被點擊後, 也會被記錄起來.
    - 若輸入的 CSS Selector 對應到超過一個元件, 則只記錄第一個.

- 輸入文字
  - 指令格式:
    - Input {text}
    - 輸入文字 {text}
  - 指令說明:
    - 輸入 {text} 文字內容, 輸入的目前為之前使用 Find 或 Click 所記錄的元件.
    - {text} 為要輸入的任意文字, 可不需雙引號.
    - 可以使用 [ENTER] 表示要輸入 Enter 鍵. (注意大小寫要相同)
    - 注意! [ENTER] 不能包含在其他文字中, 要單獨使用如: Input [ENTER].

- 下載檔案
  - 指令格式:
    - Download {url} {file-path}
    - 下載檔案 {url} {file-path}
  - 指令說明:
    - 將 {url} 指向的檔案下載並存在 {file-path}.
    - {file-path} 需包含檔案名稱. 若沒有指定路徑, 則檔案將存在目前目錄下.

### 流程控制指令

- 條件判斷
  - 指令格式:
    - If {condition}
    - 條件判斷 {condition}
  - 指令說明:
    - 若 {condition} 判斷結果為真, 則執行 If ~ End 之間的指令.
    - {condition} 語法使用一般 Java if() 語法.

- 標記 If 結束位置
  - 指令格式:
    - End
    - 結束判斷
  - 指令說明:
    - 與 If 成對出現, 標記 If 指令要執行的範圍.

- 進入迴圈
  - 指令格式:
    - Loop [{start} {end}]
    - 進入迴圈 [{start} {end}]
  - 指令說明:
    - 依 {start}, {end} 指定的區間, 執行 Loop ~ Next 之間的指令.
    - {start} 及 {end} 也可不指定, 若不指定時, Loop 到 Next 之間的指令會持續執行直到遇見 Break 指令.
    - 在 Loop ~ Next 範圍中, 可以使用 ${loop} 取得目前 loop index.

- 回到 Loop 開頭
  - 指令格式:
    - Next
    - 回到開頭
  - 指令說明:
    - 與 Loop 成對出現, 標記 Loop 指令要執行的範圍.

- 跳出迴圈
  - 指令格式:
    - Break
    - 跳出迴圈
  - 指令說明:
    - 如果目前執行位置在 Loop ~ Next 中間, 則無條件跳到 Next 下一行繼續執行.
    - 如果目前執行位置不在 Loop ~ Next 中間, 則無作用.
