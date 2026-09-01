# HTML5 实战复习：从一篇博客页面学 HTML

> 复习目标：以“一篇完整的博客文章页面”为案例，把 HTML 页面骨架、标题段落、图片链接、列表、表格、表单和语义化布局串起来复习。
>
> 建议方式：先通读完整案例，再跟着 3.x 小节逐块拆解；最后用第 5 节的清单自测。

## 1. 案例目标

我们要搭建一个“技术博客文章页”，包含：

- 站点头部（logo + 导航）
- 一篇带标题、段落、图片、列表、表格的文章正文
- 侧边“延伸阅读”
- 文章评论区表单
- 站点页脚

页面结构可以先用一棵树看明白：

```text
<body>
├── <header>            站点头部
│   ├── <a>             logo
│   └── <nav>
│       └── <ul>        主导航
├── <main>              页面主体（一个页面只有一个 main）
│   ├── <article>       文章本体
│   │   ├── <header>    文章头部：h1 + 作者/时间
│   │   ├── <section>   正文区块：段落 + figure 图片
│   │   ├── <section>   正文区块：有序列表
│   │   └── <section>   正文区块：对比表格
│   ├── <aside>         侧边补充内容：延伸阅读
│   └── <section>       评论区：form 表单
└── <footer>            站点页脚
```

## 2. 完整案例代码

把下面的代码放到一个 `.html` 文件里，用浏览器打开即可预览页面。

```html
<!DOCTYPE html>
<!-- ① 文档类型声明：告诉浏览器用现代 HTML5 标准解析页面 -->
<html lang="zh-CN">
  <!-- ② 根元素：lang 声明页面主要语言，利于搜索引擎与屏幕阅读器 -->
  <head>
    <!-- ③ 字符集声明尽量放在 head 最前面，避免中文乱码 -->
    <meta charset="UTF-8">
    <!-- ④ 视口设置：移动端按设备宽度渲染页面 -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- ⑤ 页面描述：会出现在搜索引擎摘要中 -->
    <meta name="description" content="用一篇博客文章页面复习 HTML5 语义化标签与常用元素">
    <!-- ⑥ 页面标题：显示在浏览器标签页 -->
    <title>我的技术博客 | 用一篇博客页面复习 HTML5</title>
  </head>

  <body>
    <!-- ================= 站点头部 ================= -->
    <header>
      <!-- logo 也是一个超链接，点击回到首页 -->
      <a href="/">小码的学习日志</a>

      <!-- 导航：包裹在 nav 中，列表表示导航项 -->
      <nav aria-label="主导航">
        <ul>
          <li><a href="#article">正文</a></li>
          <li><a href="#compare">对比表</a></li>
          <li><a href="#comment">评论区</a></li>
        </ul>
      </nav>
    </header>

    <!-- ================= 页面主体 ================= -->
    <main>
      <!-- 文章本体：article 表示独立、可单独传播的内容 -->
      <article id="article">
        <!-- 文章头部：文章自己的标题、作者、时间 -->
        <header>
          <h1>从一篇博客页面，重新理解 HTML5 语义化</h1>
          <p>
            作者：<a href="/about">小码</a> ·
            <!-- time 让时间可被机器识别，datetime 给出规范格式 -->
            <time datetime="2026-08-23">2026 年 8 月 23 日</time>
          </p>
        </header>

        <!-- 正文区块 1：说明 + 配图 -->
        <section>
          <h2>为什么语义化很重要</h2>
          <p>
            语义化不是“好看”，而是让结构有明确含义。浏览器、搜索引擎和辅助技术都能借助标签理解页面：哪些是导航、哪些是正文、哪些是补充信息。
          </p>

          <!-- figure 把图片和说明文字组合成一个整体 -->
          <figure>
            <img
              src="images/blog-layout.svg"
              alt="博客页面布局示意图：从上到下依次为 header、main 和 footer，main 中包括 article 与 aside"
              width="800"
              height="450"
            >
            <!-- figcaption 是图片/插图的说明文字 -->
            <figcaption>图 1：本案例的页面布局结构</figcaption>
          </figure>
          <!-- 提示：如果本地没有 images/blog-layout.svg，可以换成任意一张图片地址 -->
        </section>

        <!-- 正文区块 2：学习目标，用有序列表表示顺序 -->
        <section>
          <h2>读完这篇文章你将掌握</h2>
          <ol>
            <li>用 <code>&lt;header&gt;</code>、<code>&lt;nav&gt;</code> 搭建站点骨架；</li>
            <li>用 <code>&lt;article&gt;</code>、<code>&lt;section&gt;</code> 组织正文；</li>
            <li>用 <code>&lt;aside&gt;</code> 放侧边补充内容；</li>
            <li>用 <code>&lt;table&gt;</code> 展示对比数据；</li>
            <li>用 <code>&lt;form&gt;</code> 实现评论提交。</li>
          </ol>
        </section>

        <!-- 正文区块 3：用表格做数据对比 -->
        <section id="compare">
          <h2>常见标签对比</h2>
          <p>下表汇总本案例中出现的几组易混淆标签。</p>
          <table>
            <!-- caption：表格标题，位于 table 内部 -->
            <caption>HTML 语义化标签对比表</caption>
            <thead>
              <tr>
                <th>标签</th>
                <th>语义</th>
                <th>本案例用途</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td><code>&lt;header&gt;</code></td>
                <td>区块头部</td>
                <td>站点头部、文章头部</td>
              </tr>
              <tr>
                <td><code>&lt;nav&gt;</code></td>
                <td>导航区域</td>
                <td>站内主导航</td>
              </tr>
              <tr>
                <td><code>&lt;article&gt;</code></td>
                <td>独立成篇的内容</td>
                <td>博客正文</td>
              </tr>
              <tr>
                <td><code>&lt;aside&gt;</code></td>
                <td>与正文相关的补充内容</td>
                <td>延伸阅读</td>
              </tr>
              <tr>
                <td><code>&lt;footer&gt;</code></td>
                <td>区块底部</td>
                <td>站点页脚</td>
              </tr>
            </tbody>
          </table>
        </section>
      </article>

      <!-- 侧边补充内容：aside 放与正文相关、但不是正文的信息 -->
      <aside>
        <h2>延伸阅读</h2>
        <ul>
          <li><a href="https://developer.mozilla.org/zh-CN/docs/Web/HTML" target="_blank" rel="noopener">MDN：HTML 文档</a></li>
          <li><a href="https://html.spec.whatwg.org/" target="_blank" rel="noopener">WHATWG：HTML 标准</a></li>
        </ul>
      </aside>

      <!-- 评论区：section 分组 + form 表单 -->
      <section id="comment" aria-labelledby="comment-title">
        <h2 id="comment-title">评论区</h2>
        <form action="/api/comments" method="post">
          <p>
            <!-- label 的 for 和输入控件的 id 配对，点击文字即可聚焦输入框 -->
            <label for="nickname">昵称</label>
            <input
              type="text"
              id="nickname"
              name="nickname"
              maxlength="20"
              placeholder="例如：小码"
              required
            >
          </p>
          <p>
            <label for="email">邮箱</label>
            <!-- type="email" 会在提交时做邮箱格式校验 -->
            <input
              type="email"
              id="email"
              name="email"
              placeholder="name@example.com"
              required
            >
          </p>
          <p>
            <label for="content">评论内容</label>
            <textarea
              id="content"
              name="content"
              rows="6"
              minlength="5"
              maxlength="500"
              required
            ></textarea>
          </p>
          <p>
            <!-- button 默认 type 是 submit，点击后提交表单 -->
            <button type="submit">提交评论</button>
          </p>
        </form>
      </section>
    </main>

    <!-- ================= 站点页脚 ================= -->
    <footer>
      <p>&copy; 2026 小码的学习日志 · 用 HTML 认真记录每一天</p>
    </footer>
  </body>
</html>
```

**总结**：完整案例是一个现代 HTML5 页面：结构上按“头部、主体、页脚”组织，主体内用 `article` 承载文章、用 `aside` 放补充内容、用 `form` 放评论表单；标签的选择以“这个内容是什么”为准，而不是“这个内容长什么样”。下面逐块拆解每个知识点。

## 3. 知识点拆解

### 3.1 页面骨架：`<!DOCTYPE>`、`<html>`、`<head>`、`<body>`

任何页面的起点都是这套骨架。`head` 放“页面的配置”，`body` 放“用户看到的内容”。

```html
<!DOCTYPE html>
<!-- 声明文档类型：现代 HTML5 页面第一行必须是它 -->
<html lang="zh-CN">
  <head>
    <!-- 字符集：UTF-8 支持中文；位置越靠前越好 -->
    <meta charset="UTF-8">
    <!-- 视口：移动端页面宽度 = 设备宽度，缩放比例 1:1 -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- 标题：出现在标签页和搜索结果中 -->
    <title>我的技术博客 | 用一篇博客页面复习 HTML5</title>
  </head>
  <body>
    <!-- body 里才是用户能看到的内容 -->
  </body>
</html>
```

**总结**：页面骨架 = `<!DOCTYPE html>` + `<html>` + `<head>` + `<body>`。`lang="zh-CN"` 声明页面语言，`charset="UTF-8"` 保证中文不乱码，`viewport` 保证移动端显示正常。不写 `DOCTYPE` 可能让浏览器进入“怪异模式”，导致布局行为不可控。

### 3.2 标题与段落

博客文章最核心的内容就是“标题 + 段落”。标题用 `<h1>` 到 `<h6>` 表示从大到小的层级，段落用 `<p>`。

```html
<article>
  <header>
    <!-- 文章大标题用 h1，一个页面一般只有一个 -->
    <h1>从一篇博客页面，重新理解 HTML5 语义化</h1>
    <p>作者：小码 · 2026 年 8 月 23 日</p>
  </header>

  <section>
    <!-- 区块标题用 h2，表示它是 h1 的下级内容 -->
    <h2>为什么语义化很重要</h2>
    <p>语义化不是“好看”，而是让结构有明确含义。浏览器、搜索引擎和辅助技术都能借助标签理解页面。</p>
  </section>
</article>
```

**总结**：标题不是用来“调大字号”的，而是用来表达内容层级；它们构成文档大纲，屏幕阅读器可以直接按标题跳转。段落必须用 `<p>`，不要把大段文字直接堆在 `<div>` 里。需要更深层级时继续使用 `<h3>`、`<h4>`，不要跳级使用。

### 3.3 图片与超链接

图片用 `<img>`，跳转用 `<a>`。图片的 `alt` 属性必须认真写：它描述图片内容，图片加载失败时会显示，屏幕阅读器也会朗读它。

```html
<!-- figure + figcaption：图片和说明文字组成一个整体 -->
<figure>
  <img
    src="images/blog-layout.svg"
    alt="博客页面布局示意图：从上到下依次为 header、main 和 footer，main 中包括 article 与 aside"
    width="800"
    height="450"
  >
  <figcaption>图 1：本案例的页面布局结构</figcaption>
</figure>

<!-- 站内链接：href 使用相对路径或锚点 -->
<a href="/about">小码</a>
<a href="#comment">跳到评论区</a>

<!-- 站外链接：新窗口打开时记得加 rel="noopener" -->
<a href="https://developer.mozilla.org/zh-CN/docs/Web/HTML" target="_blank" rel="noopener">MDN 文档</a>
```

**总结**：`<img>` 必须有 `src` 和 `alt`；`width`/`height` 可以先占住尺寸，避免图片加载后页面跳动。`<a>` 通过 `href` 指定目标：站内用相对路径或 `#id` 锚点，站外用完整 URL；使用 `target="_blank"` 打开新窗口时，必须同时加 `rel="noopener"` 防止新页面反向控制当前页面。`figure` 和 `figcaption` 适合放“图文一体”的内容。

### 3.4 无序列表与有序列表

列表有两种：`<ul>` 适合顺序无关的内容，`<ol>` 适合顺序本身有意义的内容。

```html
<!-- ul：项目顺序无关，通常用于导航、菜单、一组并列内容 -->
<nav aria-label="主导航">
  <ul>
    <li><a href="#article">正文</a></li>
    <li><a href="#compare">对比表</a></li>
    <li><a href="#comment">评论区</a></li>
  </ul>
</nav>

<!-- ol：顺序有意义，浏览器会自动编号 -->
<ol>
  <li>用 <code>&lt;header&gt;</code>、<code>&lt;nav&gt;</code> 搭建站点骨架；</li>
  <li>用 <code>&lt;article&gt;</code>、<code>&lt;section&gt;</code> 组织正文；</li>
  <li>用 <code>&lt;form&gt;</code> 实现评论提交。</li>
</ol>
```

**总结**：列表项都写在 `<li>` 里，`<li>` 只能作为 `<ul>` 或 `<ol>` 的直接子元素。判断标准很简单：顺序换一下会不会改变含义？会，就用 `<ol>`；不会，就用 `<ul>`。不要在 `<div>` 里手动拼“圆点”和“数字”冒充列表，浏览器和辅助技术无法识别。

### 3.5 表格：文章数据对比

表格用来展示有行列关系的数据。结构上按“标题、表头、数据”分组：`caption`、`thead`、`tbody`。

```html
<table>
  <!-- caption 是表格标题，浏览器会显示在表格上方 -->
  <caption>HTML 语义化标签对比表</caption>
  <thead>
    <!-- thead：表头分组；th 表示“表头单元格” -->
    <tr>
      <th>标签</th>
      <th>语义</th>
      <th>本案例用途</th>
    </tr>
  </thead>
  <tbody>
    <!-- tbody：数据行分组；td 表示“普通单元格” -->
    <tr>
      <td><code>&lt;header&gt;</code></td>
      <td>区块头部</td>
      <td>站点头部、文章头部</td>
    </tr>
    <tr>
      <td><code>&lt;article&gt;</code></td>
      <td>独立成篇的内容</td>
      <td>博客正文</td>
    </tr>
    <tr>
      <td><code>&lt;aside&gt;</code></td>
      <td>侧边补充内容</td>
      <td>延伸阅读</td>
    </tr>
  </tbody>
</table>
```

**总结**：标准表格结构是 `table > caption + thead/tbody + tr + th/td`。`th` 是表头单元格，`td` 是数据单元格，`tr` 是行。把表头放进 `thead`、数据放进 `tbody`，不仅语义清晰，后续用 CSS 分组设置样式也更方便。另外，想在页面文字里显示 `<header>` 这样的代码，必须写成 `&lt;header&gt;`，否则会被浏览器当成真正的标签解析。

### 3.6 表单：评论区

表单负责“收集用户输入”。评论区的三个关键点是：`label` 与控件配对、每个提交字段都要有 `name`、利用浏览器内置的校验属性。

```html
<form action="/api/comments" method="post">
  <!-- label 的 for 与 input 的 id 一一对应：点击文字即可聚焦，屏幕阅读器也能识别 -->
  <p>
    <label for="nickname">昵称</label>
    <input type="text" id="nickname" name="nickname" maxlength="20" placeholder="例如：小码" required>
  </p>
  <p>
    <label for="email">邮箱</label>
    <!-- type="email" 让浏览器在提交前检查邮箱格式 -->
    <input type="email" id="email" name="email" placeholder="name@example.com" required>
  </p>
  <p>
    <label for="content">评论内容</label>
    <!-- textarea 是多行文本；minlength/maxlength 限制长度，required 必填 -->
    <textarea id="content" name="content" rows="6" minlength="5" maxlength="500" required></textarea>
  </p>
  <p>
    <!-- button 默认 type 是 submit，点击后按 action/method 提交 -->
    <button type="submit">提交评论</button>
  </p>
</form>
```

**总结**：`form` 用 `action` 指定提交地址、`method` 指定提交方式。每个输入控件都要有 `name`，否则提交时数据不会带上。`type="email"`、`required`、`minlength`、`maxlength` 是浏览器内置校验，能提前拦截大部分无效输入；真正的业务校验仍然需要在后端再做一次。

### 3.7 HTML5 语义化标签的布局应用

本案例的布局全靠语义化标签搭出来，没有用一堆无意义的 `<div>` 套 `<div>`。

```html
<header>
  <!-- 页面级头部 -->
  <a href="/">小码的学习日志</a>
  <nav>
    <!-- 导航区域 -->
    <ul>
      <li><a href="#article">正文</a></li>
      <li><a href="#compare">对比表</a></li>
      <li><a href="#comment">评论区</a></li>
    </ul>
  </nav>
</header>

<main>
  <!-- 页面主体，一个页面只能有一个 main -->
  <article>
    <!-- 独立成篇的内容 -->
    <section>文章区块</section>
    <section>文章区块</section>
  </article>
  <aside>
    <!-- 与正文相关但不是正文的补充内容 -->
    <h2>延伸阅读</h2>
    <ul>
      <li><a href="https://developer.mozilla.org/zh-CN/docs/Web/HTML">MDN 文档</a></li>
    </ul>
  </aside>
  <section id="comment" aria-labelledby="comment-title">
    <!-- 评论区 -->
  </section>
</main>

<footer>
  <!-- 页面级页脚 -->
  <p>&copy; 2026 小码的学习日志</p>
</footer>
```

**总结**：这些标签的定位是：`header`/`footer` 表示区块的头部和底部（页面级和区块级都可用），`nav` 表示导航，`main` 表示页面主体且全页唯一，`article` 表示可独立成篇的内容，`section` 表示主题相关的内容分组，`aside` 表示补充信息，`time` 表示可机器识别的时间。语义化布局的好处是：搜索引擎能识别正文和导航，屏幕阅读器能快速跳转，代码的意图也一眼可读。

## 4. 知识点速查表

| 知识点 | 核心标签/属性 | 案例位置 | 一句话要点 |
| --- | --- | --- | --- |
| 页面骨架 | `<!DOCTYPE html>`、`<html lang="zh-CN">`、`<head>`、`<meta>`、`<body>` | 完整案例的最外层结构 | 声明标准、语言、字符集；配置放 `head`，内容放 `body` |
| 标题与段落 | `<h1>`-`<h6>`、`<p>` | 文章头部与各区块 | 标题按层级组织文档大纲，段落用 `<p>` |
| 图片与超链接 | `<img>`、`<a>`、`<figure>`、`<figcaption>` | 文章配图、导航、延伸阅读 | `img` 必须有 `alt`；站外链接加 `target="_blank" rel="noopener"` |
| 列表 | `<ul>`、`<ol>`、`<li>` | 导航、学习目标 | 顺序无关用 `ul`，顺序有意义用 `ol` |
| 表格 | `<table>`、`<caption>`、`<thead>`、`<tbody>`、`<tr>`、`<th>`、`<td>` | 常见标签对比 | 表头 `th` 放 `thead`，数据 `td` 放 `tbody` |
| 表单 | `<form>`、`<label>`、`<input>`、`<textarea>`、`<button>` | 评论区 | `label` 的 `for` 与控件 `id` 配对；提交字段必须写 `name` |
| 语义化布局 | `<header>`、`<nav>`、`<main>`、`<article>`、`<section>`、`<aside>`、`<footer>`、`<time>` | 整个页面骨架 | 用标签表达“内容是什么”，而不是用 `div` 表达“看起来怎样” |

## 5. 复习自测

- [ ] 不看笔记，能否默写出 `<!DOCTYPE html>` 到 `</html>` 的完整页面骨架？
- [ ] 能否说清 `<head>` 与 `<body>` 的分工？
- [ ] 能否给一张图写出带 `alt` 的 `<img>`，并解释为什么必须写 `alt`？
- [ ] 能否区分什么时候用 `<ul>`、什么时候用 `<ol>`？
- [ ] 能否画出一个包含 `caption/thead/tbody/th/td` 的完整表格结构？
- [ ] 能否写出一个“昵称 + 邮箱 + 评论内容”的表单，并保证每个输入框都能点击 `label` 聚焦？
- [ ] 能否在白板上画出本案例的语义化布局树（`header/nav/main/article/aside/footer`）？

> 顺带记住：`<font>`、`<center>`、`<marquee>` 等旧标签已被废弃，样式统一交给 CSS，新代码中不要再使用。
