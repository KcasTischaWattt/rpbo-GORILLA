// src/boot/emitter.js
import mitt from 'mitt'

export const emitter = mitt()

export default ({ app }) => {
  app.config.globalProperties.$emitter = emitter
}
