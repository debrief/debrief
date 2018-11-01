var createMsi = require('msi-packager')
 
var options = {
 
  // required
  source: '/home/travis/build/debrief/org.mwc.debrief.product/target/products/DebriefNG/win32/win32/x86/DebriefNG',
  output: '/home/travis/build/debrief/org.mwc.debrief.product/target/products/DebriefNG-Windows32Bit.msi',
  name: 'DebriefNG',
  upgradeCode: '6d8fb213-e072-4f38-a184-bfbf7505307e',
  version: '3.0.454',
  manufacturer: 'Deep Blue C Technology Ltd',
  iconPath: '/home/travis/build/debrief/org.mwc.debrief.product/icon.ico',
  executable: 'DebriefNG.exe',
 
  // optional
  description: "The Debrief Maritime Analysis Tool accelerates maritime data analysis and reporting. Over twenty years of visionary development make us the first choice of effective analysts the world over.",
  arch: 'x86',
  localInstall: true
 
}

createMsi(options, function (err) {
  if (err) throw err
  console.log('MSI installer generated at ' + options.output)
})
