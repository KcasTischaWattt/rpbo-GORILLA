const imageMap = import.meta.glob('/src/assets/**/*.jpg', {
    eager: true,
    import: 'default',
  })
  
  
  export function getImageAssetPath(relativePath) {
    const normalizedPath = relativePath.replace(/^src\/assets\//, '')
    return imageMap[`/src/assets/${normalizedPath}`]
  }