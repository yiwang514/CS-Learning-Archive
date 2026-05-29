# Dream-Journal GitHub Pages 部署空白页问题修复说明

## 问题现象

将项目推送到 GitHub 并通过 GitHub Pages 部署后，打开页面是**完全空白**的，浏览器控制台也没有明显的报错。

## 根本原因：Vite 的 `base` 路径与 GitHub Pages 子目录不匹配

GitHub Pages 部署个人项目时，网站 URL 格式为：

```
https://<用户名>.github.io/<仓库名>/
```

例如本项目：`https://yiwang514.github.io/Dream-Journal/`

这意味着所有静态资源（JS、CSS、图片等）的请求路径都会带上 `/Dream-Journal/` 前缀。但 Vite 默认的 `base` 是 `/`，构建出来的 `index.html` 里引用的资源路径是：

```html
<script type="module" src="/src/main.jsx"></script>
```

浏览器实际请求的是 `https://yiwang514.github.io/src/main.jsx`，而这个路径在 GitHub Pages 上根本不存在（正确路径应该是 `/Dream-Journal/src/main.jsx`），所以所有资源加载失败，页面一片空白。

同样的问题也影响了**前端路由**：React Router 的 `BrowserRouter` 默认假设应用部署在根路径 `/`，当用户直接访问 `https://yiwang514.github.io/Dream-Journal/stats` 时，GitHub Pages 找不到这个静态文件，会返回 404。

## 我做了什么来修复

### 1. 设置 Vite 的 `base` 配置

在 `vite.config.ts` 中添加：

```ts
base: '/Dream-Journal/',
```

这会让 Vite 在构建时自动为所有资源路径加上 `/Dream-Journal/` 前缀，确保在 GitHub Pages 子目录下能正确加载。

### 2. 配置 React Router 的 `basename`

在 `App.tsx` 中设置：

```tsx
<BrowserRouter basename="/Dream-Journal">
```

这样路由就知道自己运行在 `/Dream-Journal/` 子路径下，`/stats` 实际对应 `/Dream-Journal/stats`。

### 3. 添加 GitHub Pages SPA 404 回退

GitHub Pages 不支持单页应用的"所有路径都返回 index.html"这种模式。当用户直接访问 `/Dream-Journal/stats` 时，GitHub Pages 会返回 404。

解决方案是添加一个 `public/404.html`，它会把 URL 路径编码到查询参数中，然后重定向到 `index.html`：

```html
<!-- public/404.html -->
<script>
  // 把 /Dream-Journal/stats 变成 /Dream-Journal/?/stats
  var pathSegmentsToKeep = 1;
  var l = window.location;
  l.replace(
    l.protocol + '//' + l.hostname + (l.port ? ':' + l.port : '') +
    l.pathname.split('/').slice(0, 1 + pathSegmentsToKeep).join('/') + '/?/' +
    l.pathname.slice(1).split('/').slice(pathSegmentsToKeep).join('/').replace(/&/g, '~and~') +
    (l.search ? '&' + l.search.slice(1).replace(/&/g, '~and~') : '') +
    l.hash
  );
</script>
```

然后在 `index.html` 中添加对应的恢复脚本，把查询参数还原为正确的路径：

```html
<script>
  // 把 /?/stats 还原为 /stats，然后 React Router 接管
  (function(l) {
    if (l.search[1] === '/') {
      var decoded = l.search.slice(1).split('&').map(function(s) {
        return s.replace(/~and~/g, '&')
      }).join('?');
      window.history.replaceState(null, null,
        l.pathname.slice(0, -1) + decoded + l.hash
      );
    }
  }(window.location));
</script>
```

### 4. 同步 PWA 的 `start_url`

在 `vite.config.ts` 的 VitePWA 配置中，`start_url` 也需要改为 `/Dream-Journal/`，否则 PWA 安装后打开会跳到错误的路径。

### 5. 创建 GitHub Actions 自动部署工作流

添加 `.github/workflows/deploy.yml`，每次推送到 `main` 分支时自动构建并部署到 GitHub Pages，使用官方的 `actions/deploy-pages` action。

## 总结

| 问题 | 修复 |
|------|------|
| 静态资源路径错误，全部 404 | `vite.config.ts` 设置 `base: '/Dream-Journal/'` |
| 前由刷新时 404 | `BrowserRouter` 设置 `basename="/Dream-Journal"` |
| 直接访问子路由返回 404 | 添加 `404.html` + `index.html` 中的 SPA 重定向脚本 |
| PWA 安装后路径错误 | PWA `start_url` 改为 `/Dream-Journal/'` |
| 需要手动部署 | 添加 GitHub Actions 自动构建部署工作流 |
