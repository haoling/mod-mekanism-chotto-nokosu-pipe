# ちょっと残すパイプ (Leave-a-Little Pipe)

NeoForge 1.21.1 用のミニ mod。Mekanism の Logistical Transporter とつなげられる搬出パイプで、
**品目ごとに「元に残す個数」を決め、それを超えた分だけ**搬出する（Mekanism 標準のソーターは在庫量を見られない）。

- チェストなどの横に置くと、後ろの在庫 → 前のブロック（チェスト / Logistical Transporter など標準のアイテム受け口を持つもの）へ流す。Mekanism には依存しない。
- 設定: アイテムを持って右クリック = フィルター指定。素手で右クリック = GUI（残す個数 0〜30000、1回の搬出上限 1〜64）。フィルターなしなら全アイテム対象。
- 置き方: 在庫のブロックに向けて置くと、反対側が出口。
- レシピ: 鉄インゴット7 + コンパレーター + ホッパー。

## 開発
```
./gradlew build            # build/libs/chottonokosu-<version>.jar
./gradlew runGameTestServer  # 自動テスト
```
タグ `vX.Y.Z` を push すると GitHub Release に jar が付く（`.github/workflows/release.yml`）。
mod は両側（サーバーとクライアント）への導入が必要。
