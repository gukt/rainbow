<template>
  <div id="app">
    <!--最顶层的出口，渲染最高级路由匹配到的组件-->
    <!--对于Layout组件来说，在AppMain组件中还定义了一个嵌套路由-->
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>
