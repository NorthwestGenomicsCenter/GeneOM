import { VIEW_NAMES } from '../viewNames';
import { writeFileSync } from 'fs';
import { resolve } from 'path';

const outputPath = resolve(__dirname, '../viewNames.json');
const values = Object.values(VIEW_NAMES);
writeFileSync(outputPath, JSON.stringify(values, null, 2));
console.log(`viewNames.json written to ${outputPath}`);
