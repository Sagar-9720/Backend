import axios from 'axios';

async function loadConfig(): Promise<Record<string, any>> {
  const profile = process.env.NODE_ENV || 'docker';
  const configServerUrl = process.env.SPRING_CLOUD_CONFIG_URI || 'http://config-server:8888';
  const appName = process.env.SPRING_APPLICATION_NAME || 'user-service';
  const url = `${configServerUrl}/${appName}/${profile}`;
  const response = await axios.get(url);
  // Merge all property sources, last one wins
  return response.data.propertySources.reduce((acc: Record<string, any>, src: any) => ({
    ...acc,
    ...src.source
  }), {});
}

export default loadConfig;

