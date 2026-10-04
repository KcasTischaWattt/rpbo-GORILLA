import { defineRouter } from '#q-app/wrappers'
import {
  createRouter,
  createMemoryHistory,
  createWebHistory,
  createWebHashHistory,
} from 'vue-router'
import routes from './routes'
import { backendURL } from 'src/data/lookups'

export default defineRouter(function (/* { store, ssrContext } */) {
  const createHistory = process.env.SERVER
    ? createMemoryHistory
    : process.env.VUE_ROUTER_MODE === 'history'
      ? createWebHistory
      : createWebHashHistory

  const Router = createRouter({
    scrollBehavior: () => ({ left: 0, top: 0 }),
    routes,
    history: createHistory(process.env.VUE_ROUTER_BASE),
  })

  // Глобальный guard для проверки авторизации и прав доступа
  Router.beforeEach((to, from, next) => {
    if (to.meta.checkSubViewPermission || to.meta.checkDigestViewPermission) {
      const url = to.meta.checkDigestViewPermission
        ? backendURL + `digests/${to.params.id}/check_access`
        : backendURL + `subscriptions/${to.params.id}/check_access`
      fetch(url, { credentials: 'include' })
        .then((response) => {
          if (!response.ok) {
            throw new Error('Not authorized')
          }
          return response.json()
        })
        .then((data) => {
          if (data.available) {
            return next()
          }
          return next({ name: '404' })
        })
        .catch(() => next({ name: 'home' }))
        return
    }
    if (!to.meta.requiresAuth) {
      return next()
    }

    fetch(backendURL + 'users/check_auth', { credentials: 'include' })
      .then((response) => {
        if (!response.ok) {
          throw new Error('Not authenticated')
        }
        return response.json()
      })
      .then((user) => {
        // Если маршрут требует права модератора, проверяем наличие разрешения "edit_docs"
        if (to.meta.moderatorOnly && !user.authorities.includes('edit_delete_docs')) {
          return next({ name: 'home' })
        }
        // Если маршрут требует проверки владельца подписки, выполняем дополнительный запрос
        if (to.meta.subscriptionOwner) {
          fetch(
            // `/api/subscriptions/${to.params.id}/check-owner`,
            backendURL + `subscriptions/${to.params.id}/check_ownership`,
            { credentials: 'include' },
          )
            .then((response) => {
              if (!response.ok) {
                throw new Error('Not authorized')
              }
              return response.json() // вот тут
            })
            .then((data) => {
              if (data.is_owner) {
                return next()
              }
              return next({ name: '404' })
            })
            .catch(() => next({ name: 'home' }))
          return // Выходим, т.к. next() будет вызван внутри fetch
        }
        //  разрешаем переход
        return next()
      })
      .catch(() => next({ name: 'login' }))
  })

  return Router
})
