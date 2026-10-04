const routes = [
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    children: [
      {
        path: 'login',
        name: 'login',
        component: () => import('../pages/LoginPage.vue')
      },
      {
        path: 'register',
        name: 'register',
        component: () => import('../pages/RegisterPage.vue')
      },
      {
        path: 'confirm-email',
        name: 'confirm-email',
        component: () => import('../pages/EmailConfirmPage.vue')
      },
      {
        path: '',
        name: 'home',
        component: () => import('../pages/IndexPage.vue'),
      },
      {
        path: 'documents',
        name: 'documents',
        component: () => import('../pages/SearchPage.vue'),
      },
      {
        path: 'documents/:id',
        name: 'document-view',
        component: () => import('../pages/DocumentPage.vue'),
      },
      {
        path: 'documents/:id/edit',
        name: 'document-edit',
        component: () => import('../pages/DocumentEditPage.vue'),
        meta: { requiresAuth: true, moderatorOnly: true }
      },
      {
        path: 'subscriptions/create',
        name: 'subscriptions-create',
        component: () => import('../pages/SubscriptionCreate.vue'),
      },
      {
        path: 'subscriptions/:id/edit',
        name: 'subscription-edit',
        component: () => import('../pages/SubscriptionEdit.vue'),
        meta: { requiresAuth: true, subscriptionOwner: true }
      },
      {
        path: 'subscriptions/:id',
        name: 'subscription-view',
        component: () => import('../pages/SubscriptionPage.vue'),
        meta: { checkSubViewPermission: true }
      },
      {
        path: 'digests',
        name: 'digests',
        component: () => import('../pages/DigestExpolorePage.vue'),
      },
      {
        path: 'digests/:id',
        name: 'digest-view',
        component: () => import('../pages/DigestPage.vue'),
        meta: { checkDigestViewPermission: true }
      },
      {
        path: 'stats',
        name: 'stats',
        component: () => import('../pages/StatisticsPage.vue'),
      },
      {
        path: 'profile',
        name: 'profile',
        component: () => import('../pages/IndexPage.vue'),
      },
    ],
  },

  {
    path: '/privacy',
    component: () => import('../layouts/SidebarLayout.vue'),
    children: [
      {
        path: '',
        name: 'privacy',
        component: () => import('../pages/PrivacyPage.vue'),
        meta: { requiresAuth: false }
      }
    ]
  },
    {
    path: '/landing',
    component: () => import('../layouts/LandingLayout.vue'),
    children: [
      {
        path: '',
        name: 'landing',
        component: () => import('../pages/LandingPage.vue'),
        meta: { requiresAuth: false }
      }
    ]
  },
  {
    path: '/data',
    component: () => import('../layouts/SidebarLayout.vue'),
    children: [
      {
        path: '',
        name: 'data',
        component: () => import('../pages/DataPage.vue'),
        meta: { requiresAuth: true}
      }
    ]
  },

  {
    path: '/:catchAll(.*)*',
    name: '404',
    component: () => import('../pages/ErrorNotFound.vue'),
  },
]

export default routes


/*
{
  path: 'documents/:id/edit',
  name: 'document-edit',
  component: () => import('../pages/DocumentEditPage.vue'),
  meta: { requiresAuth: true, moderatorOnly: true }
},
{
  path: 'subscriptions/:id/edit',
  name: 'subscription-edit',
  component: () => import('../pages/SubscriptionEdit.vue'),
  meta: { requiresAuth: true, subscriptionOwner: true }
},
*/