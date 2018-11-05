var createMsi = require('msi-packager')

var options = {

  // required
  source: 'org.mwc.debrief.product/target/products/DebriefNG/win32/win32/x86_64/DebriefNG',
  output: 'org.mwc.debrief.product/target/products/DebriefNG-Windows64Bit.msi',
  name: 'DebriefNG',
  upgradeCode: '6d8fb213-e072-4f38-a184-bfbf7505307e',
  version: 'versionReplacement',
  manufacturer: 'Deep Blue C Technology Ltd',
  iconPath: 'org.mwc.debrief.product/icon.ico',
  executable: 'DebriefNG.exe',

  // optional
  description: "The Debrief Maritime Analysis Tool accelerates maritime data analysis and reporting. Over twenty years of visionary development make us the first choice of effective analysts the world over.",
  arch: 'x64',
  localInstall: true

}

createMsi(options, function (err) {
  if (err) throw err
  console.log('MSI installer generated at ' + options.output)
})
