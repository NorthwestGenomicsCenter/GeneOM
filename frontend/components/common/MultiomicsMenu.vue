<template>
  <ul class="menu">
    <template v-if="workspace != null">
      <li>
        <h3 class="menu-section">Workspaces</h3>
      </li>
      <template v-for="item in workspace.recentAnalysisItems" :key="item.name">
        <MenuItem
            :to="`/analysis/view-${item.id}`"
            icon=""
            :text="item.name" />
      </template>
      <MenuItem
          v-if="workspace.totalAnalyses > 3"
          icon=""
          text="More..."
          @click="viewAssaysDialog = true" />
    </template>

    <!-- Begin Data Explorer Features-->
    <li>
      <h3 class="menu-section">Explore Data</h3>
    </li>
    <MenuItem to="/Explore/Type" text="Find by type" icon="" />
    <MenuItem to="/Samples/" text="Find Samples" icon="" />
    <!-- End Data Explorer Features-->

    <li>
      <h3 class="menu-section">Manage</h3>
    </li>
    <MenuItem to="/manage/analysis" text="Analysis" icon="" />
    <MenuItem to="/manage/cohorts" text="Cohorts" icon="" />
    <MenuItem to="/manage/samples" text="Samples" icon="" />
    <MenuItem to="/manage/tags" text="Tags" icon="" />
    <MenuItem to="/import/ImportExternalData" text="Import External Data" icon="" />

    <template v-if="isAdmin">
      <li>
        <h3 class="menu-section">Admin</h3>
      </li>
      <MenuItem to="/manage/users" text="Manage Users" icon="" />
    </template>

  </ul>

  <Dialog header="Available Assays" v-model:visible="viewAssaysDialog" modal>
    <div class="p-2">
      <RecentAnalysisTable />
    </div>
  </Dialog>
</template>

<script lang="ts" setup>
import {useAuth} from "~/composables/useAuth";
import MenuItem from "~/components/common/MenuItem.vue";
import RecentAnalysisTable from "~/components/common/RecentAnalysisTable.vue";

const router = useRouter();
const isCurrentPage = computed(() => {
  return router.currentRoute.value.fullPath
})

const auth = useAuth();
const user = auth.user;
const isAdmin = computed(() => {
  return auth?.user?.value?.permissions?.includes("ROLE_ADMIN")
})

defineOptions({
  name: 'Multiomics-Menu'
})

const { data: workspace, refresh } = useApi<RecentAnalysisDTO>(`/analysis/recent`, {
  method: "GET",
  key: 'recent-analysis-projects',
  default: () => null
});

//-----------------------------------------------
// View additional assays for the user
const viewAssaysDialog = ref<boolean>(false);

</script>

<style>
body {
  font-family: "Roboto", sans-serif;
  font-weight: 400;
  font-style: normal;
  margin: 0px;
  min-height: 100% !important;

  --p-button-primary-background: #56498e;
  --p-button-primary-border-color: #6b5cb2;
  --p-button-primary-hover-background: #4a407a;
  --p-button-primary-hover-border-color: #56498e;

  --toolbar-font-color: #333;
  --header-height: 5em;
  --footer-height: 4em;
}
</style>

<style scoped>
.menu {
  margin-top: 1.5em;
  background-color: #fff;
}

.navbar ul {
  padding: 0;
}

.sidebar ul {
  list-style-type: none;
  padding: 0;
  display: grid;
}

ul {
  padding: 0;
}

li {
  list-style: none;
  padding: 0;
  text-align: center;
}

.menu-section {
  font-size: medium;
  text-align: left;
  padding-left: 1em;
  text-transform: uppercase;
  color: #666;
  font-weight: 900;
}

</style>