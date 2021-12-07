Vue.component("vue-user-select", {
	props: ['photoDomain'],
	template:
		"<div class='dialog-mask' v-show='show'>" +
		"    <div class='dailog choose-dialog'>" +
		"        <div class='header' style='padding:unset;'>" +
		"            <span>选择人员</span>" +
		"            <i class='close' @click='show = false'></i>" +
		"        </div>" +
		"        <div class='body'>" +
		"            <div class='choose-list fl'>" +
		"                <div class='search'>" +
		"                    <el-input type='text' class='search-ipt' v-model.trim='sw' @focus='swFocus' @blur='swBlur' placeholder='姓名/手机号' @keyup.enter.native='search'></el-input>" +
		"                    <i class='icon close' v-show='sw' @click='resetSearch'></i>" +
		"                    <i class='icon search-icon' @click='search'></i>" +
		"                </div>" +
		"                <div class='crumb' v-show='!searching'>" +
		"                    <ul class='clear'>" +
		"                        <li :class=\"{'back-item': (index < crumbs.length - 1)}\" v-for='(crumb, index) of crumbs' :key='\"crumb-\" + index'>" +
		"                           <template v-if='index < crumbs.length - 1'><span @click='changeCrumb(index)'>{{crumb.name}}</span><i class='icon-right'></i></template>" +
		"                           <template v-else>{{crumb.name}}</template>" +
		"                        </li>" +
		"                    </ul>" +
		"                </div>" +
		"                <div class='catalog-list' id='mescroll-container'>" +
		"                   <div>" +
		/*机构列表*/
			"                    <div class='catalog-item' v-show=\"dataType == 'org' && !searching\" v-for='(org, index) of orgs' @click='expandOrg(index)'>" +
			"                        <div class='catalog-photo'>" +
			"                            <img :src=\"ctx + '/pc/assets/images/catalog1.png'\">" +
			"                        </div>" +
			"                        <div class='catalog-title'>{{org.name}}</div>" +
			"                        <img class='right-img' :src=\"ctx + '/pc/assets/images/chevron-right.png'\">" +
			"                    </div>" +
			/*部门列表*/
			"                    <div class='catalog-item' v-show=\"dataType == 'department' && !searching\" v-for='(department, index) of departments' :key=\"'department-' + index\" @click='expandDepartment(department)'>" +
			"                        <div class='catalog-photo'>" +
			"                            <img :src=\"ctx + '/pc/assets/images/catalog2.png'\">" +
			"                        </div>" +
			"                        <div class='catalog-title'>{{department.name}}</div>" +
			"                        <img class='right-img' :src=\"ctx + '/pc/assets/images/chevron-right.png'\">" +
			"                    </div>" +
			/*部门用户列表*/
								"<div :class=\"{'catalog-item': true, 'bgc-blue': (user.checked)}\" v-show=\"dataType == 'user' && !searching\" v-for='(user, index) of departmentUsers' :key=\"'user-' + index\">" +
								"    <div class='catalog-photo'>" +
								"        <img :src=\"photoDomain + '/p/' + user.puid + '_80'\">" +
								"    </div>" +
								"    <div class='catalog-title' @click='chooseDepartmentUser(index)'>{{user.name}}</div>" +
								"    <el-checkbox class='right-check' v-model='user.checked' @change='checkDepartmentUser(index)'></el-checkbox>" +
								"</div>" +
		/*搜索用户列表*/
								"<div :class=\"{'catalog-item': true, 'bgc-blue': (user.checked)}\" v-show='searching' v-for='(user, index) of searchUsers' :key=\"'search-user-' + index\">" +
								"    <div class='catalog-photo'>" +
								"        <img :src=\"photoDomain + '/p/' + user.puid + '_80'\">" +
								"    </div>" +
								"    <div class='catalog-title'>{{user.name}}</div>" +
								"    <el-checkbox class='right-check' v-model='user.checked' @change='checkSearchUser(index)'></el-checkbox>" +
								"</div>" +
				"                <div class='empty-content' v-show=\"((dataType == 'user' && departmentUsersLoaded && departmentUsers.length < 1) && !searching) || (searching && searchLoaded && searchUsers.length < 1)\">" +
				"                    无结果" +
				"                </div>" +
		"                   </div>" +
		"                </div>" +
		"            </div>" +
		"            <div class='item-choosed fl'>" +
		"                <div class='choose-number'>已选&nbsp;{{selectedUsers.length}}&nbsp;人</div>" +
		"                <div class='catalog-list'>" +
		"                    <div class='catalog-item' v-for='(user, index) of selectedUsers' :key=\"'selected-user-' + index\">" +
		"                        <div class='catalog-photo'>" +
		"                            <img :src=\"photoDomain + '/p/' + user.puid + '_80'\">" +
		"                        </div>" +
		"                        <div class='catalog-title'>{{user.name}}</div>" +
		"                        <div class='del-img' @click='remove(index)'></div>" +
		"                    </div>" +
		"                </div>" +
		"            </div>" +
		"        </div>" +
		"        <div class='footer'>" +
		"            <div class='btn-box'>" +
		"                <div class='border-btn' @click='show = false'>取消</div>" +
		"                <div class='normal-btn' @click='sure'>确定</div>" +
		"            </div>" +
		"        </div>" +
		"    </div>" +
		"</div>",
	data: function () {
		return {
			ctx: ctx,
			show: false,
			// 顶部面包屑
			crumbs: [
				{name: "全部", type: ""}
			],
			// 机构列表
			orgs: [],
			// 某个机构下的所有的部门
			allDepartments: [],
			// 当前左侧列表的数据类型
			dataType: "org",
			// 左侧的部门列表
			departments: [],
			// 部门下的用户列表
			departmentUserQueryParams: {
				pageNum: 1,
				pageSize: 10
			},
			departmentUsersLoaded: false,
			departmentUsers: [],
			// 搜索的用户
			searching: false,
			sw: "",
			searchUserQueryParams: {
				pageNum: 1,
				pageSize: 1000,
				sw: "",
			},
			searchLoaded: false,
			searchUsers: [],
			// 选中的用户
			selectedUsers: [],
			mescroll: null
		};
	},
	created: function () {
		var $this = this;
		$this.loadOrgs();
	},
	watch: {
		"searching": function () {
			var $this = this;
			if ($this.searching && $this.mescroll) {
				// 隐藏"-到底了-"
				$this.mescroll.endUpScroll(false);
			}
		}
	},
	methods: {
		//加载机构列表
		loadOrgs: function () {
			var $this = this;
			var url = ctx + "/api/wfw/org/include-contacts";
			app.ajaxGet(url, function (data) {
				if (data.success) {
					$this.orgs = data.data;
				} else {
					var message = data.message;
					if (activityApp.isEmpty(message)) {
						message = "加载机构列表失败";
					}
					app.showMsg(message);
				}
			}, function () {

			});
		},
		// 展开机构
		expandOrg: function (index) {
			var $this = this;
			var org = $this.orgs[index];
			org.type = "org";
			$this.crumbs.push(org);
			$this.dataType = "department";
			var url = ctx + "/api/wfw/org/" + org.fid + "/department";
			app.ajaxPost(url, {}, function (data) {
				if (data.success) {
					$this.allDepartments = data.data.records;
					// 筛选level为1的部门列表
					$($this.allDepartments).each(function () {
						if (this.level == 1) {
							$this.departments.push(this);
						}
					});
				} else {
					var errorMessage = data.message;
					if (activityApp.isEmpty(errorMessage)) {
						errorMessage = "加载部门失败";
					}
					app.showMsg(errorMessage);
				}
			}, function () {
				app.showMsg("加载部门失败");
			});
		},
		// 展开部门
		expandDepartment: function (department) {
			var $this = this;
			department.type = "department";
			$this.crumbs.push(department);
			var subdeptcount = department.subdeptcount;
			if (subdeptcount < 1) {
				// 查询部门下的用户
				$this.dataType = "user";
				$this.initMescroll();
			} else {
				// 加载部门
				$this.dataType = "department";
				$this.departments = [];
				$($this.allDepartments).each(function () {
					if (this.pid == department.id) {
						$this.departments.push(this);
					}
				});
			}
		},
		initMescroll: function () {
			var $this = this;
			if (activityApp.isEmpty($this.mescroll)) {
				$this.mescroll = new MeScroll("mescroll-container", {
					//上拉加载的配置项
					up: {
						callback: function () {
							$this.loadDepartmentUsers();
						},
						isBounce: false,
						htmlNodata: '<p class="upwarp-nodata">-- 到底了 --</p>',
						noMoreSize: 3,
						lazyLoad: {
							use: false
						}
					}
				});
			} else {
				$this.resetLeftSide();
			}
		},
		resetLeftSide: function () {
			var $this = this;
			$this.departments = [];
			$this.departmentUsers = [];
			$this.departmentUserQueryParams.pageNum = 1;
			if ($this.mescroll) {
				$this.mescroll.destroy();
				$this.mescroll = null;
			}
		},
		loadDepartmentUsers: function () {
			var $this = this;
			// 当前选中的部门
			var department = $this.crumbs[$this.crumbs.length - 1];
			var url = ctx + "/api/wfw/department/" + department.id + "/contacter";
			var params = {
				pageNum: $this.departmentUserQueryParams.pageNum,
				pageSize: $this.departmentUserQueryParams.pageSize
			};
			$this.departmentUsersLoaded = false;
			app.ajaxPost(url, params, function (data) {
				if (data.success) {
					$this.departmentUsersLoaded = true;
					$this.departmentUserQueryParams.pageNum++;
					var departmentUsers = data.data.records;
					$(departmentUsers).each(function () {
						this.checked = $this.checkedFill(this.puid);
					});
					$this.departmentUsers.pushArray(departmentUsers);
					$this.mescroll.endBySize(departmentUsers.length, data.data.total);
				} else {
					var message = data.message;
					if (activityApp.isEmpty(message)) {
						message = "加载部门下用户失败";
					}
					app.showMsg(message);
				}
			}, function () {
				app.showMsg("加载部门下用户失败");
			});
		},
		checkedFill: function (uid) {
			var $this = this;
			var checked = false;
			$($this.selectedUsers).each(function () {
				if (this.puid == uid) {
					checked = true;
					return false;
				}
			});
			return checked;
		},
		changeCrumb: function (index) {
			var $this = this;
			var crumb = $this.crumbs[index];
			var type = crumb.type;
			$this.resetLeftSide();
			$this.crumbs = $this.crumbs.slice(0, index > 0 ? index : 1);
			console.log($this.crumbs);
			console.log(index);
			switch (type) {
				case "org":
					$this.dataType = "department";
					$this.expandOrg($this.getOrgIndex(crumb));
					break;
				case "department":
					$this.expandDepartment(crumb);
					break;
				default:
					// 全部
					$this.dataType = "org";
			}
		},
		getOrgIndex: function (org) {
			var $this = this;
			var index = 0;
			$($this.orgs).each(function (i) {
				if (this.fid == org.fid) {
					index = i;
					return false;
				}
			});
			return index;
		},
		chooseDepartmentUser: function (index) {
			var $this = this;
			var user = $this.departmentUsers[index];
			user.checked = !user.checked;
			$this.checkDepartmentUser(index);
		},
		checkDepartmentUser: function (index) {
			var $this = this;
			var user = $this.departmentUsers[index];
			if (user.checked) {
				// 选中
				$this.selectedUsers.push(user);
			} else {
				// 取消选中
				$this.selectedUsers.splice($this.getSelectedUserIndex(user.puid), 1);
			}
		},
		checkSearchUser: function (index) {
			var $this = this;
			var user = $this.searchUsers[index];
			if (user.checked) {
				// 选中
				$this.selectedUsers.push(user);
			} else {
				// 取消选中
				$this.selectedUsers.splice($this.getSelectedUserIndex(user.puid), 1);
			}
		},
		getSelectedUserIndex: function (uid) {
			var $this = this;
			var index = 0;
			$($this.selectedUsers).each(function (i) {
				if (uid == this.puid) {
					index = i;
					return false;
				}
			});
			return index;
		},
		remove: function (index) {
			var $this = this;
			var user = $this.selectedUsers[index];
			$this.selectedUsers.splice(index, 1);
			$($this.departmentUsers).each(function (i) {
				if (user.puid == this.puid) {
					$this.$set($this.departmentUsers[i], "checked", false);
					return false;
				}
			});
			$($this.searchUsers).each(function (i) {
				if (user.puid == this.puid) {
					$this.$set($this.searchUsers[i], "checked", false);
					return false;
				}
			});
		},
		swFocus: function () {
			var $this = this;
			$this.searching = true;
		},
		swBlur: function () {
			var $this = this;
			if (activityApp.isEmpty($this.sw)) {
				$this.searching = false;
			}
		},
		resetSearch: function () {
			var $this = this;
			$this.sw = "";
			$this.swBlur();
		},
		search: function () {
			var $this = this;
			var url = ctx + "/api/wfw/contacter/search";
			$this.searchUserQueryParams.sw = $this.sw;
			$this.searchLoaded = false;
			app.ajaxPostWithLoading(url, $this.searchUserQueryParams, function (data) {
				if (data.success) {
					$this.searchLoaded = true;
					var searchUsers = data.data.records;
					$(searchUsers).each(function () {
						this.checked = $this.checkedFill(this.puid);
					});
					$this.searchUsers = searchUsers;
				} else {
					var message = data.message;
					if (activityApp.isEmpty(message)) {
						message = "查询用户失败";
					}
					app.showMsg(message);
				}
			}, function () {
				app.showMsg("查询用户失败");
			});
		},
		sure: function () {
			var $this = this;
			if ($this.selectedUsers.length < 1) {
				app.showMsg("请选择人员");
				return;
			}
			$this.show = false;
			$this.$emit("callback");
		}
	}
});