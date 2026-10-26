## AREA 1:
### TC-SIDE-01/TC-SIDE-02:
- Link Home
  - Absolute: "/html/body/angular-spotify-root/as-layout/as-nav-bar/ul/li[1]/a"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-nav-bar-1]//*[@x-test-tpl-ul-3]//*[@x-test-hook-li-4][1]//*[@x-test-hook-a-5]"
  - Katalon: "(.//*[normalize-space(text()) and normalize-space(.)='Search'])[1]/preceding::a[1]"
  - Relative: "//a[normalize-space()='Home']"
  - RobulaPlus:
  - Robula:
  - Selenium: "//a[contains(text(),'Home')]"
- Link Search  
  - Absolute: "/html/body/angular-spotify-root/as-layout/as-nav-bar/ul/li[2]/a"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-nav-bar-1]//*[@x-test-tpl-ul-3]//*[@x-test-hook-li-4][2]//*[@x-test-hook-a-5]"
  - Katalon: "(.//*[normalize-space(text()) and normalize-space(.)='Home'])[1]/following::a[1]"
  - Relative: "//a[normalize-space()='Search']"
  - RobulaPlus:
  - Robula:
  - Selenium: "//a[contains(text(),'Search')]"
- Link Browse
  - Absolute: "/html/body/angular-spotify-root/as-layout/as-nav-bar/ul/li[3]/a"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-nav-bar-1]//*[@x-test-tpl-ul-3]//*[@x-test-hook-li-4][3]//*[@x-test-hook-a-5]"
  - Katalon: "(.//*[normalize-space(text()) and normalize-space(.)='Search'])[1]/following::a[1]"
  - Relative: "//a[normalize-space()='Browse']"
  - RobulaPlus:
  - Robula:
  - Selenium: "//a[contains(text(),'Browse')]"
- Link MyPlaylists
  - Absolute: "/html/body/angular-spotify-root/as-layout/as-nav-bar/ul/li[4]/a"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-nav-bar-1]//*[@x-test-tpl-ul-3]//*[@x-test-hook-li-4][4]//*[@x-test-hook-a-5]"
  - Katalon: "(.//*[normalize-space(text()) and normalize-space(.)='Browse'])[1]/following::a[1]"
  - Relative: "//a[normalize-space()='My Playlists']"
  - RobulaPlus: "//*[@ng-reflect-router-link='/collection/playlists']"
  - Robula: "//a[@ng-reflect-router-link='/collection/playlists']"
  - Selenium: "//a[contains(text(),'My Playlists')]"
- Link MyAlbums
  - Absolute: "/html/body/angular-spotify-root/as-layout/as-nav-bar/ul/li[5]/a"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-nav-bar-1]//*[@x-test-tpl-ul-3]//*[@x-test-hook-li-4][5]//*[@x-test-hook-a-5]"
  - Katalon: "(.//*[normalize-space(text()) and normalize-space(.)='My Playlists'])[1]/following::a[1]"
  - Relative: "//a[normalize-space()='My Albums']"
  - RobulaPlus:
  - Robula:
  - Selenium: "//a[contains(text(),'My Albums')]"
- Link LikedSongs
  - Absolute: "/html/body/angular-spotify-root/as-layout/as-nav-bar/ul/li[6]/a"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-nav-bar-1]//*[@x-test-tpl-ul-3]//*[@x-test-hook-li-4][6]//*[@x-test-hook-a-5]"
  - Katalon: "(.//*[normalize-space(text()) and normalize-space(.)='My Albums'])[1]/following::a[1]"
  - Relative: "//a[normalize-space()='Liked songs']"
  - RobulaPlus:
  - Robula:
  - Selenium: "//a[contains(text(),'Liked songs')]"

## AREA 2:
### Pre-conditions (not mutated):
- Link LikedSongs
  - Absolute: "/html/body/angular-spotify-root/as-layout/as-nav-bar/ul/li[6]/a"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-nav-bar-1]//*[@x-test-tpl-ul-3]//*[@x-test-hook-li-4][6]//*[@x-test-hook-a-5]"
  - Katalon: "(.//*[normalize-space(text()) and normalize-space(.)='My Albums'])[1]/following::a[1]"
  - Relative: "//a[normalize-space()='Liked songs']"
  - RobulaPlus:
  - Robula:
  - Selenium: "//a[contains(text(),'Liked songs')]"
- Song Card
  - Absolute: "/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-tracks/div/div/as-playlist-track[1]/as-media-table-row/as-track-main-info"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-main-view-3]//*[@x-test-tpl-div-2]//*[@x-test-tpl-div-1]//*[@x-test-hook-as-playlist-track-11][1]//*[@x-test-tpl-as-media-table-row-1]//*[@x-test-hook-as-track-main-info-4]"
  - Katalon: "xpath=(.//*[normalize-space(text()) and normalize-space(.)='Date added'])[1]/following::as-track-main-info[1]"
  - Relative: "//as-playlist-track[1]//as-media-table-row[1]//as-track-main-info[1]"
  - RobulaPlus: 
  - Robula: 
  - Selenium: "//as-track-main-info"
### TC-PLAY-01:
- Button next
  - Absolute: "/html/body/angular-spotify-root/as-layout/as-now-playing-bar/footer/div[2]/as-player-controls/div/div[2]"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-now-playing-bar-4]//*[@x-test-tpl-footer-1]//*[@x-test-hook-as-player-controls-5]//*[@x-test-tpl-div-1]//*[@x-test-hook-div-5]"
  - Katalon: "xpath=(.//*[normalize-space(text()) and normalize-space(.)='Stop'])[1]/preceding::div[13]"
  - Relative: "//div[@class='now-playing-bar-center']//div[2]"
  - RobulaPlus:
  - Robula:
  - Selenium: "//as-player-controls/div/div[2]"
- Button previous
  - Absolute: "/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-now-playing-bar[1]/footer[1]/div[2]/as-player-controls[1]/div[1]/div[1]"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-now-playing-bar-4]//*[@x-test-tpl-footer-1]//*[@x-test-hook-as-player-controls-5]//*[@x-test-tpl-div-1]//*[@x-test-hook-div-2]"
  - Katalon: "xpath=(.//*[normalize-space(text()) and normalize-space(.)='Stop'])[1]/preceding::div[14]"
  - Relative: "//div[@class='flex justify-center']//div[1]"
  - RobulaPlus: 
  - Robula: 
  - Selenium: "//as-player-controls/div/div"
### TC-PLAY-02:
- Button play/pause
  - Absolute: "/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-now-playing-bar[1]/footer[1]/div[2]/as-player-controls[1]/div[1]/as-play-button[1]/button[1]"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-now-playing-bar-4]//*[@x-test-tpl-footer-1]//*[@x-test-hook-as-player-controls-5]//*[@x-test-tpl-div-1]//*[@x-test-hook-as-play-button-4]//*[@x-test-tpl-button-1]"
  - Katalon: "(.//*[normalize-space(text()) and normalize-space(.)='Stop'])[1]/preceding::button[2]"
  - Relative: "//button[@class='flex play-button control-button text-black bg-white']"
  - RobulaPlus:
  - Robula:
  - Selenium: "//as-player-controls/div/as-play-button/button"
- Animated icon (Getting the parent then searching for the svg)
  - Absolute: "/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-now-playing-bar[1]/footer[1]/div[3]/as-visualization-toggle[1]/div[1]"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-now-playing-bar-4]//*[@x-test-tpl-footer-1]//*[@x-test-hook-as-visualization-toggle-8]//*[@x-test-tpl-div-1]"
  - Katalon: "xpath=(.//*[normalize-space(text()) and normalize-space(.)='Stop'])[1]/preceding::div[7]"
  - Relative: "//body/angular-spotify-root/as-layout/as-now-playing-bar/footer/div/as-visualization-toggle/div[1]"
  - RobulaPlus: 
  - Robula: 
  - Selenium: "//as-visualization-toggle/div"
### TC-PLAY-03:
(Katalon e Selenium non restituiscono locatori funzionali)
### TC-PLAY-04:
(Katalon e Selenium non restituiscono locatori funzionali)

## AREA 3:
### Pre-conditions:
- Link MyAlbums
  - Absolute: "/html/body/angular-spotify-root/as-layout/as-nav-bar/ul/li[5]/a"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-nav-bar-1]//*[@x-test-tpl-ul-3]//*[@x-test-hook-li-4][5]//*[@x-test-hook-a-5]"
  - Katalon: "(.//*[normalize-space(text()) and normalize-space(.)='My Playlists'])[1]/following::a[1]"
  - Relative: "//a[normalize-space()='My Albums']"
  - RobulaPlus:
  - Robula:
  - Selenium: "//a[contains(text(),'My Albums')]"
### TC-LIB-01:
- Playlist Card
  - Absolute: "/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-playlists/div/as-playlist-list/div/as-card[1]/a"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-main-view-3]//*[@x-test-tpl-div-2]//*[@x-test-tpl-div-1]//*[@x-test-hook-as-playlist-list-2]//*[@x-test-tpl-div-1]//*[@x-test-hook-as-card-2][1]//*[@x-test-tpl-a-1]"
  - Katalon: "(.//*[normalize-space(text()) and normalize-space(.)='Source Code'])[1]/following::h2[1]"
  - Relative: "//as-card[@ng-reflect-title='The Goats']//a"
  - RobulaPlus: 
  - Robula:
  - Selenium: "(//a[contains(@href, '/playlist/0vhooTWkjMKTZXvnXthXdo')])[2]"
- Playlist Title
  - Absolute: "/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-main-view[1]/div[2]/as-playlist[1]/div[1]/as-media-summary[1]/div[1]/h2[1]"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-main-view-3]//*[@x-test-tpl-div-2]//*[@x-test-tpl-div-1]//*[@x-test-hook-as-media-summary-2]//*[@x-test-tpl-div-2]//*[@x-test-hook-h2-4]"
  - Katalon: "xpath=(.//*[normalize-space(text()) and normalize-space(.)='Playlist'])[1]/following::h2[1]"
  - Relative: "//as-media-summary[@type='Playlist']//div//h2"
  - RobulaPlus:
  - Robula:
  - Selenium: "css=.media-title"
- Playlist sidebar
  - Absolute: "/html/body/angular-spotify-root/as-layout/as-nav-bar/as-nav-links/ul/li[1]/as-nav-link/a"
  - Hook:
  - Katalon:
  - Relative:
  - RobulaPlus:
  - Robula:
  - Selenium:
### TC-LIB-02:
- Song count
  - Absolute: "/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-main-view[1]/div[2]/as-playlist[1]/div[1]/as-media-summary[1]/div[1]/div[2]/div[3]"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-main-view-3]//*[@x-test-tpl-div-2]//*[@x-test-tpl-div-1]//*[@x-test-hook-as-media-summary-2]//*[@x-test-tpl-div-2]//*[@x-test-hook-div-10]"
  - Katalon: "xpath=(.//*[normalize-space(text()) and normalize-space(.)='Title'])[1]/preceding::div[3]"
  - Relative: "//as-media-summary[@type='Playlist']//div[3]"
  - RobulaPlus:
  - Robula:
  - Selenium: "css=.media-info"
- Song list (Get the table then the list items)
  - Absolute: "/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-playlist/div[2]/"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-main-view-3]//*[@x-test-tpl-div-2]//*[@x-test-tpl-div-5]"
  - Katalon: "xpath=(.//*[normalize-space(text()) and normalize-space(.)='Simon'])[2]/following::div[4]"
  - Relative: "//body/angular-spotify-root/as-layout/as-main-view/div/as-playlist/div[2]"
  - RobulaPlus: 
  - Robula: 
  - Selenium: "xpath=//as-playlist/div[2]"
### TC-LIB-03:
- First song card  
  - Absolute: "/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-playlist/div[2]/div/as-playlist-track[1]/as-media-table-row/as-track-main-info"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-main-view-3]//*[@x-test-tpl-div-2]//*[@x-test-tpl-div-5]//*[@x-test-hook-as-playlist-track-14][1]//*[@x-test-tpl-as-media-table-row-1]//*[@x-test-hook-as-track-main-info-4]"
  - Katalon: "xpath=(.//*[normalize-space(text()) and normalize-space(.)='Album'])[1]/following::as-track-main-info[1]"
  - Relative: "//as-playlist-track[1]//as-media-table-row[1]//as-track-main-info[1]"
  - RobulaPlus:
  - Robula:
  - Selenium: "xpath=//as-track-main-info" 
- First song card name
  - Absolute: "/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-playlist/div[2]/div/as-playlist-track[1]/as-media-table-row/as-track-main-info/div[2]/div[1]"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-main-view-3]//*[@x-test-tpl-div-2]//*[@x-test-tpl-div-5]//*[@x-test-hook-as-playlist-track-14][1]//*[@x-test-tpl-as-media-table-row-1]//*[@x-test-hook-as-track-main-info-4]//*[@x-test-tpl-div-3]//*[@x-test-hook-div-4]"
  - Katalon: "xpath=(.//*[normalize-space(text()) and normalize-space(.)='Album'])[1]/following::div[7]"
  - Relative: "//as-playlist-track[1]//as-media-table-row[1]//as-track-main-info[1]//div[2]//div[1]"
  - RobulaPlus: 
  - Robula: 
  - Selenium: "xpath=//as-track-main-info/div[2]/div"
### TC-LIB-04:
- Now playing name  
  - Absolute: "/html/body/angular-spotify-root/as-layout/as-now-playing-bar/footer/div[1]/as-track-current-info/div[2]/div[1]/a"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-now-playing-bar-4]//*[@x-test-tpl-footer-1]//*[@x-test-hook-as-track-current-info-3]//*[@x-test-tpl-div-3]//*[@x-test-hook-a-5]"
  - Katalon: "(.//*[normalize-space(text()) and normalize-space(.)='Jan 28, 2026'])[5]/following::a[1]"
  - Relative: "//a[@class='text-white hover:underline']"
  - RobulaPlus: "//*[@class='text-white hover:underline']"
  - Robula: "//a[@class='text-white hover:underline']"
  - Selenium: ".ellipsis-one-line > .text-white"
- Second song card name
  - Absolute: "/html[1]/body[1]/angular-spotify-root[1]/as-layout[1]/as-main-view[1]/div[2]/as-playlist[1]/div[2]/div[1]/as-playlist-track[2]/as-media-table-row[1]/as-track-main-info[1]/div[2]/div[1]"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-main-view-3]//*[@x-test-tpl-div-2]//*[@x-test-tpl-div-5]//*[@x-test-hook-as-playlist-track-14][2]//*[@x-test-tpl-as-media-table-row-1]//*[@x-test-hook-as-track-main-info-4]//*[@x-test-tpl-div-3]//*[@x-test-hook-div-4]"
  - Katalon: "xpath=(.//*[normalize-space(text()) and normalize-space(.)='Michael Jackson'])[2]/preceding::div[1]"
  - Relative: "//as-playlist-track[2]//as-media-table-row[1]//as-track-main-info[1]//div[2]//div[1]"
  - RobulaPlus: 
  - Robula: 
  - Selenium: "css=.control-button:nth-child(3)"
- Button next
  - Absolute: "/html/body/angular-spotify-root/as-layout/as-now-playing-bar/footer/div[2]/as-player-controls/div/div[2]"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-now-playing-bar-4]//*[@x-test-tpl-footer-1]//*[@x-test-hook-as-player-controls-5]//*[@x-test-tpl-div-1]//*[@x-test-hook-div-5]"
  - Katalon: "xpath=(.//*[normalize-space(text()) and normalize-space(.)='Stop'])[1]/preceding::div[13]"
  - Relative: "//div[@class='now-playing-bar-center']//div[2]"
  - RobulaPlus: 
  - Robula: 
  - Selenium: "xpath=//as-player-controls/div/div[2]"

## AREA 4:
### Pre-conditions:
- Link Search
  - Absolute: "/html/body/angular-spotify-root/as-layout/as-nav-bar/ul/li[2]/a"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-nav-bar-1]//*[@x-test-tpl-ul-3]//*[@x-test-hook-li-4][2]//*[@x-test-hook-a-5]"
  - Katalon: "(.//*[normalize-space(text()) and normalize-space(.)='Home'])[1]/following::a[1]"
  - Relative: "//a[normalize-space()='Search']"
  - RobulaPlus:
  - Robula:
  - Selenium: "//a[contains(text(),'Search')]"
- Search input
  - Absolute: "/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-search/div/div[1]/as-input/div/input"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-main-view-3]//*[@x-test-tpl-div-2]//*[@x-test-tpl-div-1]//*[@x-test-hook-as-input-3]//*[@x-test-tpl-div-1]//*[@x-test-hook-input-4]"
  - Katalon: "xpath=//input"
  - Relative: "//input[@placeholder='Artists, songs, albums, or playlists']"
  - RobulaPlus: 
  - Robula: 
  - Selenium: "css=.ng-touched"
### TC-SRCH-01/TC-SRCH-02:
- Song list (Get the parent then the list):
  - Absolute: "/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-search/div/div[2]/div/as-album-track[INSERT-ITEM-NUM-HERE]/as-media-table-row/as-track-main-info/div[2]/div[1]"
  - Hook: "//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-main-view-3]//*[@x-test-tpl-div-2]//*[@x-test-tpl-div-1]//*[@x-test-hook-as-album-track-13][INSERT-ITEM-NUM-HERE]//*[@x-test-tpl-as-media-table-row-1]//*[@x-test-hook-as-track-main-info-4]//*[@x-test-tpl-div-3]//*[@x-test-hook-div-4]"
  - Katalon: "xpath=(.//*[normalize-space(text()) and normalize-space(.)='Title'])[INSERT-ITEM-NUM-HERE]/following::as-track-main-info[1]"
  - Relative: "//body/angular-spotify-root/as-layout[@class='ng-star-inserted']/as-main-view/div[@class='main-view']/as-search[@class='ng-star-inserted']/div[@class='mb-6 content-spacing']/div[2]"
  - RobulaPlus:
  - Robula:
  - Selenium:



//    {
//      "name": "PlaylistNavLink",
//      "file_path": "/home/simon/Documents/Projects/angular-spotify/libs/web/shell/ui/nav-links/src/lib/nav-link/nav-link.component.html",
//      "target_matcher": {
//        "type": "attribute",
//        "key": "[class]",
//        "value": "{ 'ellipsis-one-line': true, 'pr-6': isPlaying }"
//      }
//    }