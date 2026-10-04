import { ref } from 'vue'

const drawerOpen = ref(false)

const toggleDrawer = () => {
  drawerOpen.value = !drawerOpen.value
}

export default function useDrawer() {
  return { drawerOpen, toggleDrawer }
}
