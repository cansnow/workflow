<template>
  <div id="app">
    <Editor msg="Welcome to Your Vue.js App"/>
  </div>
</template>

<script>
import Editor from './components/Editor'

export default {
  name: 'App',
  components: {
    Editor
  }
}
</script>

<style>
body{margin: 0;	width: 100vw;	height: 100vh;}
#app {
	width: 100%;
	height: 100%;
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
</style>
